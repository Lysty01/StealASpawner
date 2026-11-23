# Rapport de développement — Phase 1.2 : gestion de la configuration YAML

| | |
|---|---|
| **Projet** | Steal a Spawner — plugin Paper 1.21.11 |
| **Phase** | 1.2 — Configuration YAML, validation, rechargement et documentation |
| **Date** | 23/11/2026 |
| **Version** | `0.1.0-SNAPSHOT` (inchangée, aucune release) |
| **Branche** | `feature/phase-1.2-config` (depuis `develop`, `e3e100e`) |
| **Pull Request** | [#4](https://github.com/Lysty01/StealASpawner/pull/4) vers `develop` (non fusionnée) |
| **Statut** | ✅ Terminée — ⏳ en attente de validation |

---

## Résumé

| Indicateur | Résultat |
|---|---|
| Compilation (`./gradlew clean build`) | ✅ BUILD SUCCESSFUL |
| Tests automatisés | ✅ **47 / 47** (25 chargeur, 9 messages, 9 commande, 4 cycle de vie) |
| `./gradlew checkDocs` | ✅ 29 fichiers Markdown, 126 liens locaux, 18 pages GitBook, 1 commande, 1 permission |
| Test sur serveur Paper 1.21.11 réel | ✅ 2 scénarios, tous les comportements attendus observés |
| Code Java hors périmètre | Aucun (pas de spawners, d'économie, de collecteurs, de factions ni de base de données) |
| Versions Paper / Java | Inchangées (1.21.11 / 21) |

---

## 1. Objectif

Doter le plugin d'un système de configuration YAML professionnel :
- création des fichiers par défaut ;
- chargement et validation stricte ;
- rechargement transactionnel via `/sas reload` ;
- messages personnalisables ;
- tests complets et documentation GitBook à jour.

Aucune fonctionnalité de gameplay n'est développée dans cette phase.

## 2. Architecture créée

```
src/main/java/com/stealaspawner/
├── StealASpawnerPlugin.java        Assemble les modules (aucune logique métier)
├── config/
│   ├── ConfigFile.java             5 fichiers gérés + version de format supportée (1)
│   ├── ConfigManager.java          Disque, création des défauts, chargement transactionnel, publication
│   ├── ConfigValidator.java        Validation pure, sans I/O → snapshot ou erreurs
│   ├── ConfigurationSnapshot.java  État validé immuable (records)
│   ├── ConfigIssue.java            Diagnostic structuré
│   ├── ConfigValidationException.java
│   ├── ReloadResult.java           Résultat détaillé d'un reload
│   ├── LogLevel.java               WARNING / INFO / DEBUG
│   ├── MessageKey.java             Clés stables de messages.yml
│   └── MessageManager.java         MiniMessage, préfixe, placeholders sûrs
├── command/
│   └── StealASpawnerCommand.java   /stealaspawner, alias /sas
└── logging/
    └── PluginLog.java              Niveau de journalisation configurable
```

Trois classes ont été ajoutées par rapport à l'architecture suggérée :
- `ConfigFile`, `MessageKey` et `LogLevel` : des enums qui portent les listes fermées (fichiers, clés, niveaux) ;
- `ConfigIssue` et `ReloadResult` : des diagnostics typés ;
- `PluginLog` : le niveau de log configurable devait s'appliquer à tout le plugin, pas seulement à la configuration.

Le détail se trouve dans [`docs/ARCHITECTURE.md`](../ARCHITECTURE.md).

## 3. Fichiers ajoutés

| Fichier | Rôle |
|---|---|
| `src/main/java/com/stealaspawner/config/*.java` (10 fichiers) | Gestion de la configuration et des messages |
| `src/main/java/com/stealaspawner/command/StealASpawnerCommand.java` | Commande `/sas` |
| `src/main/java/com/stealaspawner/logging/PluginLog.java` | Journalisation |
| `src/main/resources/config.yml` | Réglages généraux |
| `src/main/resources/messages.yml` | Messages (anglais) |
| `src/main/resources/spawners.yml`, `economy.yml`, `collectors.yml` | Fichiers réservés (seulement `config-version`) |
| `src/test/java/com/stealaspawner/config/ConfigManagerTest.java` | 25 tests du chargeur et du validateur |
| `src/test/java/com/stealaspawner/config/MessageManagerTest.java` | 9 tests des messages |
| `src/test/java/com/stealaspawner/command/StealASpawnerCommandTest.java` | 9 tests de la commande (MockBukkit) |
| `src/test/java/com/stealaspawner/testutil/CapturingLog.java` | Capture des logs pour les tests |
| `gitbook/administration/configuration.md` | Fichiers, règles de chargement, référence de `config.yml` |
| `gitbook/administration/messages.md` | Référence de `messages.yml` |
| `gitbook/administration/reserved-files.md` | Fichiers réservés et règles officielles |
| `gitbook/administration/troubleshooting.md` | Dépannage, dont toutes les erreurs YAML et de configuration |
| `gitbook/commands/sas-reload.md` | Fiche de la commande (modèle complet) |
| `docs/rapports/RAPPORT_PHASE_1.2.md` | Ce rapport |

## 4. Fichiers modifiés

| Fichier | Modification |
|---|---|
| `StealASpawnerPlugin.java` | Initialise la configuration, les messages et la commande ; désactivation propre en cas de configuration invalide |
| `plugin.yml` | Commande `stealaspawner` (alias `sas`), permission `stealaspawner.admin.reload` (`op`) |
| `StealASpawnerPluginTest.java` | Cycle de vie étendu (fichiers créés, démarrage invalide, arrêt propre) |
| `gitbook/SUMMARY.md` | 5 nouvelles pages dans la navigation |
| `gitbook/README.md`, `commands/README.md`, `permissions/README.md`, `administration/README.md`, `getting-started/installation.md`, `getting-started/feature-status.md`, `changelog/README.md`, `economy/`, `spawners/`, `collectors/` | Mises à jour (pas de doublon) |
| `CHANGELOG.md`, `docs/ROADMAP.md`, `docs/ARCHITECTURE.md`, `docs/DEVELOPMENT_LOG.md` | Patchnotes et suivi |

Aucun fichier n'a été supprimé.

## 5. Fonctionnement du gestionnaire

1. **Fichiers gérés** : l'enum `ConfigFile` liste `config.yml`, `messages.yml`, `spawners.yml`, `economy.yml` et `collectors.yml`.
2. **Création** : un fichier absent est copié depuis le jar. Un fichier existant n'est jamais écrasé, réécrit ni supprimé. Le plugin ne fait que **lire** les fichiers de l'utilisateur, donc ses commentaires et sa mise en forme sont conservés.
3. **Lecture** : chaque fichier est lu en UTF-8 dans un `YamlConfiguration` temporaire. Une erreur de lecture, d'encodage ou de syntaxe YAML (avec ligne et colonne) devient une erreur.
4. **Validation** (`ConfigValidator`, sans aucun accès disque) :

| Situation | Politique |
|---|---|
| Type incorrect, valeur non autorisée, texte obligatoire vide, `config-version` ≠ 1 | ❌ Erreur |
| Valeur obligatoire absente (`config-version`, `general.language`, `language` de `messages.yml`) | ❌ Erreur |
| Langue de `messages.yml` ≠ `general.language` | ❌ Erreur (cohérence entre fichiers) |
| Valeur optionnelle absente (`general.prefix`, `logging.level`, `diagnostics.startup-summary`) | ⚠️ Défaut du jar + avertissement |
| Message absent | ⚠️ Message intégré + avertissement |
| Clé inconnue | ⚠️ Avertissement, ignorée, **laissée dans le fichier** |

Une valeur invalide n'est **jamais** remplacée en silence par une valeur par défaut. Toutes les erreurs sont signalées en une seule fois, au format demandé :

```
[StealASpawner] Configuration error:
File: config.yml
Path: general.language
Reason: unsupported language
Expected: en
Actual: xx
```

Les valeurs affichées sont tronquées à 80 caractères. Les fichiers ne contiennent aucun secret.

5. **Lecture par les autres composants** : `configManager().snapshot()` renvoie un `ConfigurationSnapshot` immuable et complet.
6. **Démarrage invalide** : une `ConfigValidationException` est levée, les erreurs sont journalisées, puis le plugin se désactive (`disablePlugin`). Les fichiers ne sont pas touchés.

### Paramètres disponibles

| Fichier | Chemin | Type | Défaut | Valeurs acceptées |
|---|---|---|---|---|
| tous | `config-version` | entier | `1` | `1` |
| `config.yml` | `general.prefix` | texte MiniMessage | `[StealASpawner]` violet | tout texte, `""` = sans préfixe |
| `config.yml` | `general.language` | texte | `en` | `en` |
| `config.yml` | `logging.level` | texte | `INFO` | `WARNING`, `INFO`, `DEBUG` |
| `config.yml` | `diagnostics.startup-summary` | booléen | `true` | `true`, `false` |
| `messages.yml` | `language` | texte | `en` | identique à `general.language` |
| `messages.yml` | 8 messages | texte MiniMessage | anglais | texte non vide |

## 6. Fonctionnement du reload

1. Les 5 fichiers sont lus dans des objets temporaires (les fichiers absents sont recréés).
2. Tout est validé, y compris la cohérence entre fichiers.
3. S'il n'y a aucune erreur, le nouveau `ConfigurationSnapshot` est publié **en une seule affectation** (champ `volatile`), puis le niveau de log est appliqué.
4. S'il y a une erreur, l'ancien snapshot reste actif (même instance), le niveau de log ne change pas, et un `ReloadResult(success=false, errors, warnings, durée)` est renvoyé.

Les chargements sont sérialisés par un verrou. Aucun lecteur ne peut voir un état partiel. Seule la configuration est rechargée : aucun service de gameplay ni aucune base de données n'existent encore.

## 7. Commandes et permissions

| Élément | Valeur |
|---|---|
| Commande | `/stealaspawner reload`, alias `/sas reload` |
| Permission | `stealaspawner.admin.reload` — défaut `op` (refusée aux joueurs ordinaires) |
| Console | Autorisée |
| `/sas` seul | Usage (si autorisé) ou permission refusée |
| Sous-commande inconnue | `command.unknown-subcommand` (si autorisé), sinon permission refusée |
| Arguments en trop | `command.invalid-syntax` |
| Complétion | `reload` proposé uniquement aux utilisateurs autorisés |
| Erreur inattendue | `error.internal` envoyé à l'expéditeur ; la stack trace reste dans la console |

Aucune autre commande n'a été créée.

## 8. Tests exécutés

### 8.1 Tests automatisés : 47 réussis, 0 échec

| # | Scénario obligatoire | Test(s) |
|---|---|---|
| 1 | Premier démarrage, fichiers absents | `firstStartLoadsDefaultsWhenFilesAreMissing` |
| 2 | Création des fichiers par défaut | `createsEveryDefaultFileIdenticalToTheBundledResource`, `createsOnlyMissingFilesWithoutOverwritingExistingOnes`, `pluginEnablesAndCreatesTheDefaultFiles` |
| 3 | Chargement de valeurs personnalisées | `loadsCustomValues`, `emptyPrefixIsAllowed` |
| 4 | Conservation après redémarrage simulé | `customSettingsSurviveARestartAndFilesAreNeverRewritten` |
| 5 | YAML invalide | `invalidYamlIsReportedWithItsLocationAndTheFileIsLeftUntouched`, `failedReloadWithBrokenYamlKeepsThePreviousSnapshot` |
| 6 | Mauvais type de valeur | `wrongValueTypeIsAnError`, `sectionReplacedByAValueIsReportedOnce`, `unsupportedLogLevelIsAnError`, `unsupportedConfigVersionIsAnError`, `unsupportedLanguageProducesTheDocumentedDiagnostic` |
| 7 | Valeur obligatoire manquante | `missingRequiredValueIsAnError`, `emptyRequiredValueIsAnError` |
| 8 | Reload réussi | `successfulReloadPublishesANewSnapshot`, `playerWithPermissionReloads` |
| 9 | Reload échoué, ancien snapshot conservé | `failedReloadKeepsThePreviousSnapshot`, `failedReloadReportsTheErrorCountAndKeepsTheOldSnapshot` |
| 10 | Message inexistant | `unknownMessageReturnsAVisibleFallbackAndWarnsOnce`, `messageMissingFromTheFileUsesTheBuiltInDefaultWithAWarning` |
| 11 | Placeholder remplacé | `placeholdersAreReplaced` |
| 12 | Placeholder avec balisage MiniMessage non fiable | `placeholderValuesAreNeverParsedAsMiniMessage` |
| 13 | Permission de reload refusée | `playerWithoutPermissionCannotReload`, `playerWithoutPermissionGetsNoPermissionForAnySubcommand` |
| 14 | Permission de reload accordée | `playerWithPermissionReloads`, `operatorHasThePermissionByDefault` |
| 15 | Reload depuis la console | `consoleCanReload`, `usageUnknownSubcommandAndInvalidSyntaxMessages` |
| 16 | Complétion selon la permission | `tabCompletionOnlyOffersReloadToAllowedSenders` |
| — | Désactivation propre | `pluginDisablesCleanly`, `snapshotIsUnavailableBeforeLoadAndAfterShutdown` |
| — | Démarrage avec configuration invalide | `invalidConfigurationAtStartupDisablesThePluginWithoutTouchingTheFile` |
| — | Autres | clés inconnues, valeurs optionnelles absentes, cohérence des langues, erreurs multiples, résumé, mode DEBUG, préfixe, déclarations de `plugin.yml` |

Les tests sont isolés (dossier temporaire `@TempDir` ou dossier temporaire MockBukkit, horloge fixe) et n'utilisent ni réseau, ni serveur distant, ni base de données.

### 8.2 Tests sur serveur Paper 1.21.11 réel (build 132, Java 21)

**Scénario 1 : démarrage valide, puis reloads** (commandes envoyées depuis la console)

| Étape | Log observé | Résultat |
|---|---|---|
| Premier démarrage | `Created default config.yml` … `collectors.yml`, `Configuration loaded: 5 files, language=en, log level=INFO (33 ms).`, `StealASpawner 0.1.0-SNAPSHOT enabled.` | ✅ |
| `sas reload` (valide) | `[StealASpawner] Configuration reloaded (5 files, 4 ms).` | ✅ |
| Préfixe `[TEST]` + `DEBUG`, `sas reload` | `[TEST] Configuration reloaded (5 files, 4 ms).` | ✅ Nouvel état appliqué |
| `language: xx`, `sas reload` | Bloc `Configuration error` (format exact), `Reload failed with 1 error(s). The previous configuration is still active.`, puis `[TEST] Reload failed: 1 error(s)…` | ✅ **Ancien état conservé** (préfixe `[TEST]` et mode DEBUG toujours actifs) |
| `sas`, `sas fly`, `sas reload now` | `[TEST] Usage: /sas reload`, `[TEST] Unknown subcommand: fly…`, `[TEST] Invalid syntax…` | ✅ |
| Correction, `stealaspawner reload` | `[TEST] Configuration reloaded (5 files, 3 ms).` | ✅ Alias fonctionnel |
| `stop` | `StealASpawner disabled.` | ✅ Aucune erreur, aucune stack trace |

**Scénario 2 : démarrage avec `language: xx`**

| Étape | Résultat |
|---|---|
| Démarrage | Bloc `Configuration error`, `StealASpawner cannot start: 1 configuration error(s), listed above…`, puis `StealASpawner disabled.` ✅ |
| `config.yml` | Empreinte SHA-256 identique avant et après : **fichier intact** ✅ |
| `sas reload` sur le plugin désactivé | Réponse de Bukkit : `plugin is disabled` (comportement standard, documenté) |

**Non testé sur serveur réel** : les permissions côté joueur, car aucun client Minecraft n'est disponible dans l'environnement. Elles sont couvertes par les tests MockBukkit 13, 14 et 16.

## 9. Résultats de compilation

```
./gradlew clean build
> Task :compileJava
> Task :checkDocs
Documentation OK: 29 Markdown files, 126 local links, 18 GitBook pages, 1 commands and 1 permissions checked.
> Task :test
BUILD SUCCESSFUL
```

Jar : `build/libs/StealASpawner-0.1.0-SNAPSHOT.jar` (44 Ko). Il contient les 5 YAML par défaut et aucune dépendance embarquée.

## 10. Problèmes rencontrés

| # | Problème |
|---|---|
| 1 | GitBook a réécrit `gitbook-docs.yaml` sur `develop` (3 commits `GITBOOK-SITE`) : commentaires et guillemets supprimés, et surtout `language: fr` alors que le wiki est rédigé en anglais. |
| 2 | Un test supposait que MockBukkit nomme le dossier de données `StealASpawner` ; il utilise en réalité `StealASpawner-<version>`. |
| 3 | La console Paper filtre les niveaux de log inférieurs à INFO : un niveau `FINE` classique n'apparaîtrait jamais. |
| 4 | Quand le plugin est désactivé, Bukkit intercepte lui-même `/sas` et journalise `plugin is disabled`. |
| 5 | `checkDocs` faisait échouer le build tant que la commande et la permission n'étaient pas documentées. C'est le comportement attendu, et la preuve que le contrôle fonctionne. |

## 11. Corrections appliquées

| # | Correction |
|---|---|
| 1 | Laissé en l'état : c'est un réglage GitBook qui revient au responsable du projet (§ 15). |
| 2 | Le test prépare le dossier `StealASpawner-0.1.0-SNAPSHOT`. |
| 3 | Le mode `DEBUG` écrit des lignes préfixées `[DEBUG]` au niveau INFO, ce qui est documenté. |
| 4 | Le cas est documenté dans `gitbook/administration/troubleshooting.md` (solution : corriger, puis redémarrer). |
| 5 | La fiche commande, la permission et les pages de configuration ont été ajoutées dans le même commit que le code. |

## 12. Commits

| Hash | Message |
|---|---|
| `132c6e6` | `feat(config): add YAML configuration manager with strict validation` |
| `fc28fe8` | `feat(command): add /sas reload command, reload permission and GitBook documentation` |
| `6dc4781` | `docs: update changelog, roadmap, architecture and development log for phase 1.2` |
| *(ce rapport)* | `docs: add phase 1.2 development report` |

Le build et les tests ont été vérifiés à l'état de chaque commit de code.

## 13. Limites connues

- **Langue** : seul `en` est disponible. Le choix de la langue est validé, mais il n'existe qu'un seul `messages.yml`.
- **Placeholders inconnus** : un `<tag>` non reconnu dans un message s'affiche tel quel. Il n'est pas signalé comme erreur.
- **Thread** : le chargement a lieu sur le thread appelant (démarrage, commande). C'est acceptable pour 5 petits fichiers lus en quelques millisecondes, et uniquement sur une action explicite. À revoir si de gros fichiers apparaissent.
- **`logging.level: WARNING`** masque aussi les messages INFO du plugin au démarrage (résumé, message d'activation).
- **Plugin désactivé** après un démarrage invalide : `/sas reload` est impossible et il faut redémarrer le serveur. C'est une contrainte de Bukkit.
- **Clés inconnues** : un simple avertissement, pas une erreur. Une faute de frappe sur une clé optionnelle revient donc à utiliser la valeur par défaut, mais un avertissement nomme la clé.
- **Fichiers réservés** : ils ne contiennent que `config-version`, conformément à la consigne.
- Les permissions côté joueur n'ont pas été testées sur un serveur réel (pas de client disponible).

## 14. Statut de la Pull Request

| Élément | Statut |
|---|---|
| Source → destination | `feature/phase-1.2-config` → `develop` |
| Titre | `feat: Phase 1.2 - YAML configuration management` |
| Lien | [#4](https://github.com/Lysty01/StealASpawner/pull/4) |
| CI GitHub Actions | Workflow *Build & Test* (compilation, 47 tests, `checkDocs`) exécuté sur la PR |
| Fusion | ❌ Non fusionnée : en attente de validation |

## 15. Éléments restant à valider

1. **Relire et valider** la Pull Request.
2. **Langue de l'espace GitBook** : `gitbook-docs.yaml` contient `language: fr` (réglé dans GitBook) alors que le wiki est en anglais. Cela influence la langue de l'interface GitBook et l'attribut de langue des pages. À confirmer, ou à repasser en `en`, dans GitBook ou dans le dépôt.
3. **Choix fonctionnels à confirmer** :
   - permission `op` par défaut ;
   - clés inconnues traitées comme avertissements ;
   - message absent remplacé par le message intégré (avec un avertissement).
4. **Test joueur réel** de `/sas reload`, si un serveur de test avec client est disponible.
5. Instructions de la **phase 1.3**.

---

## Vérification de la documentation (règle permanente)

| Question | Réponse |
|---|---|
| Tous les fichiers YAML documentés ? | ✅ `configuration.md`, `messages.md`, `reserved-files.md` |
| Chaque paramètre, ses défauts, ses valeurs acceptées et ses conséquences ? | ✅ |
| Erreurs possibles et dépannage YAML ? | ✅ `troubleshooting.md` |
| `/sas reload` (fiche complète selon le modèle) ? | ✅ `commands/sas-reload.md` |
| `stealaspawner.admin.reload` (6 colonnes, exemple LuckPerms) ? | ✅ |
| Exemples de configuration et bonnes pratiques ? | ✅ |
| Spawners, économie et collecteurs présentés comme non disponibles ? | ✅ « Reserved » / « Planned » |
| Pages mises à jour plutôt que dupliquées ? | ✅ |
| `checkDocs` | ✅ OK |
| Modification depuis l'éditeur web GitBook ? | ❌ Aucune : tout passe par GitHub |
