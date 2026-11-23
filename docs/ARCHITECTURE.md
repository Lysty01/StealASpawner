# Architecture — Steal a Spawner

## État actuel (phase 1.1)

```
StealASpawner/
├── .github/workflows/build.yml       CI : compilation + tests sur PR et push
├── build.gradle.kts                  Build Gradle (Kotlin DSL)
├── settings.gradle.kts
├── gradle.properties                 group + version du projet
├── gradlew, gradlew.bat, gradle/     Wrapper Gradle 9.9.2
├── gradle/docs-check.gradle.kts      Tâche checkDocs (contrôle de la documentation)
├── .gitbook.yaml                     Configuration GitBook Git Sync
├── gitbook/                          Wiki officiel (anglais), voir docs/DOCUMENTATION.md
├── src/
│   ├── main/
│   │   ├── java/com/stealaspawner/
│   │   │   └── StealASpawnerPlugin.java   Point d'entrée (JavaPlugin)
│   │   └── resources/plugin.yml           Descripteur du plugin
│   └── test/java/com/stealaspawner/
│       └── StealASpawnerPluginTest.java   Tests MockBukkit
└── docs/                             Documentation du projet
```

### Choix techniques

- **Paper API 1.21.11**, en `compileOnly` : fournie par le serveur, non embarquée dans le jar.
- **Java 21** via la toolchain Gradle (version exigée par Paper 1.21.11).
- **`plugin.yml`** classique (format Bukkit), la version est injectée par Gradle à partir de `gradle.properties`.
- **LuckPerms et Oraxen** déclarés en `softdepend` : chargés avant StealASpawner s'ils sont présents, jamais obligatoires.
- **Tests** : JUnit 6 + MockBukkit, qui simule un serveur pour vérifier le cycle de vie du plugin sans lancer Minecraft. La classe principale n'est pas `final` car MockBukkit doit pouvoir la sous-classer.

## Principes pour la suite (architecture modulaire cible)

Ces principes guideront les phases suivantes. Rien n'est implémenté pour l'instant.

1. **Classe principale mince** : `StealASpawnerPlugin` se contente de créer et d'ordonner les modules ; aucune logique métier.
2. **Un module par domaine** dans son propre sous-package (`com.stealaspawner.<module>`), avec un cycle de vie commun (activation / désactivation) et des dépendances explicites entre modules.
3. **Logique métier séparée de Bukkit** autant que possible, pour pouvoir la tester unitairement sans serveur.
4. **Intégrations optionnelles isolées** (LuckPerms, Oraxen) : chargées uniquement si le plugin correspondant est présent, avec repli sur un comportement par défaut.
5. **Performance (~400 joueurs)** : entrées/sorties asynchrones, rien de bloquant sur le thread principal, production calculée à la demande à partir d'horodatages.
