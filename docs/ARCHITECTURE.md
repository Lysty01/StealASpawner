# Architecture — Steal a Spawner

## État actuel (phase 1.2)

```
StealASpawner/
├── .github/workflows/build.yml       CI : compilation + tests + checkDocs sur PR et push
├── build.gradle.kts                  Build Gradle (Kotlin DSL)
├── settings.gradle.kts
├── gradle.properties                 group + version du projet
├── gradlew, gradlew.bat, gradle/     Wrapper Gradle 9.9.2
├── gradle/docs-check.gradle.kts      Tâche checkDocs (contrôle de la documentation)
├── gitbook-docs.yaml                 GitBook Git Sync : site → espace (racine ./)
├── .gitbook.yaml                     GitBook Git Sync : espace → pages dans gitbook/
├── gitbook/                          Wiki officiel (anglais), voir docs/DOCUMENTATION.md
├── src/
│   ├── main/
│   │   ├── java/com/stealaspawner/
│   │   │   ├── StealASpawnerPlugin.java        Point d'entrée : assemble les modules, aucune logique métier
│   │   │   ├── config/
│   │   │   │   ├── ConfigFile.java             Liste des 5 fichiers gérés + version de format supportée
│   │   │   │   ├── ConfigManager.java          Disque : création des défauts, lecture, chargement transactionnel, publication
│   │   │   │   ├── ConfigValidator.java        Validation pure (sans I/O) → snapshot ou liste d'erreurs
│   │   │   │   ├── ConfigurationSnapshot.java  État validé immuable (records)
│   │   │   │   ├── ConfigIssue.java            Diagnostic (fichier, chemin, raison, attendu, obtenu)
│   │   │   │   ├── ConfigValidationException.java
│   │   │   │   ├── ReloadResult.java           Résultat détaillé d'un reload
│   │   │   │   ├── LogLevel.java               WARNING / INFO / DEBUG
│   │   │   │   ├── MessageKey.java             Clés stables de messages.yml + placeholders
│   │   │   │   └── MessageManager.java         Rendu MiniMessage, préfixe, placeholders sûrs
│   │   │   ├── command/
│   │   │   │   └── StealASpawnerCommand.java   /stealaspawner (alias /sas) : reload + complétion
│   │   │   └── logging/
│   │   │       └── PluginLog.java              Logger du plugin avec niveau configurable
│   │   └── resources/
│   │       ├── plugin.yml                      Commande, alias, permission
│   │       └── config.yml, messages.yml, spawners.yml, economy.yml, collectors.yml   Défauts
│   └── test/java/com/stealaspawner/            JUnit 6 + MockBukkit (47 tests)
└── docs/                             Documentation du projet
```

### Flux de configuration

```
Démarrage / /sas reload
   │
   ▼
ConfigManager.attempt()
   ├─ crée les fichiers absents depuis le jar (jamais d'écrasement)
   ├─ lit chaque fichier (UTF-8) → YamlConfiguration temporaire
   │     erreur de lecture / YAML → ConfigIssue ERROR
   ▼
ConfigValidator.validate(fichiers, défauts)   ← aucune I/O
   ├─ ERROR  : type, valeur, champ obligatoire, version, cohérence des langues
   ├─ WARNING: clé inconnue, valeur optionnelle absente (défaut utilisé)
   ▼
0 erreur ? ── oui ──► publication atomique du nouveau ConfigurationSnapshot (champ volatile)
   │                   + application du niveau de log
   non
   ├─ démarrage : ConfigValidationException → plugin désactivé proprement
   └─ reload    : ancien snapshot conservé, ReloadResult(success=false, errors)
```

### Choix techniques

- **Paper API 1.21.11**, en `compileOnly` : fournie par le serveur, non embarquée dans le jar.
- **Java 21** via la toolchain Gradle (version exigée par Paper 1.21.11).
- **`plugin.yml`** classique (format Bukkit), la version est injectée par Gradle à partir de `gradle.properties`. La commande est déclarée dans `plugin.yml` (exécuteur Bukkit `TabExecutor`), ce qui permet aussi à `checkDocs` de vérifier qu'elle est documentée.
- **LuckPerms et Oraxen** déclarés en `softdepend` : chargés avant StealASpawner s'ils sont présents, jamais obligatoires.
- **Configuration** : `YamlConfiguration` de Bukkit pour lire, **jamais pour écrire** (les fichiers utilisateur ne sont jamais réécrits). Validation séparée du disque pour être testable unitairement.
- **État immuable** : `ConfigurationSnapshot` (records, `Map.copyOf`) publié via un champ `volatile` ; les lecteurs obtiennent toujours un état complet. Chargements sérialisés par un verrou.
- **Messages** : Adventure / MiniMessage fournis par Paper (aucune dépendance ajoutée). Valeurs de placeholders insérées avec `Placeholder.unparsed` : jamais interprétées comme balisage.
- **Journalisation** : le niveau `DEBUG` écrit des lignes `[DEBUG]` au niveau INFO, car la console Paper filtre les niveaux inférieurs à INFO.
- **Thread** : chargement sur le thread appelant (démarrage, commande). Cinq petits fichiers, quelques millisecondes, uniquement sur action explicite.
- **Tests** : JUnit 6 + MockBukkit. Tests unitaires du chargeur dans des dossiers temporaires (`@TempDir`), tests de commande et de cycle de vie avec MockBukkit. La classe principale n'est pas `final` car MockBukkit doit pouvoir la sous-classer.

### API interne pour les phases suivantes

| Besoin | API |
|---|---|
| Lire la configuration | `plugin.configManager().snapshot()` → `ConfigurationSnapshot` |
| Envoyer un message | `plugin.messageManager().send(audience, MessageKey.X, MessageManager.placeholder("nom", valeur))` |
| Ajouter un réglage | Défaut dans `src/main/resources/<fichier>.yml` + champ dans `ConfigurationSnapshot` + règle dans `ConfigValidator` + tests + page GitBook |
| Ajouter un message | Défaut dans `messages.yml` + constante `MessageKey` + ligne dans `gitbook/administration/messages.md` |

## Principes pour la suite (architecture modulaire cible)

1. **Classe principale mince** : `StealASpawnerPlugin` se contente de créer et d'ordonner les modules ; aucune logique métier. ✅ appliqué
2. **Un module par domaine** dans son propre sous-package (`com.stealaspawner.<module>`), avec un cycle de vie commun (activation / désactivation) et des dépendances explicites entre modules. ✅ `config`, `command`, `logging`
3. **Logique métier séparée de Bukkit** autant que possible, pour pouvoir la tester unitairement sans serveur. ✅ `ConfigValidator`
4. **Intégrations optionnelles isolées** (LuckPerms, Oraxen) : chargées uniquement si le plugin correspondant est présent, avec repli sur un comportement par défaut.
5. **Performance (~400 joueurs)** : entrées/sorties asynchrones pour les accès répétés, rien de bloquant sur le thread principal, production calculée à la demande à partir d'horodatages.
