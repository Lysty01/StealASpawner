# Changelog

Toutes les modifications notables du projet sont consignées ici.

Le format suit [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/) et le projet utilise le [versionnage sémantique](https://semver.org/lang/fr/).

## [Non publié]

### Ajouté — Documentation GitBook

- Wiki officiel **Steal a Spawner — Official Wiki** en anglais dans `gitbook/` (13 pages : accueil, démarrage, installation, état des fonctionnalités, économie, spawners, collecteurs, factions, pillage, commandes, permissions, administration, changelog).
- `.gitbook.yaml` pour la synchronisation GitBook ↔ GitHub (Git Sync).
- Tâche Gradle `checkDocs`, exécutée par `./gradlew build` et par la CI : liens Markdown locaux, pages manquantes du `SUMMARY.md`, commandes et permissions de `plugin.yml` non documentées.
- Règles de maintenance de la documentation (`docs/DOCUMENTATION.md`) et modèle de fiche commande (`docs/templates/COMMAND_TEMPLATE.md`).

### Ajouté — Phase 1.1 : initialisation du projet

- Projet Gradle (Kotlin DSL, wrapper 9.9.2) ciblant Paper 1.21.11 et Java 21.
- Classe principale `com.stealaspawner.StealASpawnerPlugin` avec `onEnable()` / `onDisable()`.
- Descripteur `plugin.yml` (version injectée depuis Gradle, `softdepend` LuckPerms et Oraxen).
- Tests de démarrage et d'arrêt du plugin avec JUnit 6 et MockBukkit.
- Documentation : README, cahier des charges, roadmap, architecture, journal de développement.
- Intégration continue GitHub Actions : compilation et tests sur chaque Pull Request.
- Workflow Git `main` / `develop` / `feature/*` / `fix/*` et Conventional Commits.
- Rapport de fin d'étape `docs/rapports/RAPPORT_PHASE_1.1.md`.

### Corrigé

- CI : `./gradlew: Permission denied` (code 126) sur GitHub Actions. `gradlew` est maintenant enregistré comme exécutable dans Git (mode `100755`) et le workflow exécute `chmod +x ./gradlew` avant la compilation.
