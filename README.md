# Steal a Spawner

Plugin Minecraft **Paper 1.21.11** pour le mini-jeu *Steal a Spawner* : chaque joueur fait produire des spawners dans sa base, accumule de l'argent… et peut aller voler les spawners, l'argent et les collecteurs des autres.

> **Statut : phase 1.1 — initialisation.** Le plugin démarre et s'arrête proprement, aucune fonctionnalité de jeu n'est encore implémentée. Voir la [roadmap](docs/ROADMAP.md).

## Caractéristiques techniques

| Élément | Valeur |
|---|---|
| Serveur cible | Paper 1.21.11 (`paper-api 1.21.11-R0.1-SNAPSHOT`) |
| Java | 21 (toolchain Gradle) |
| Build | Gradle 9.9.2 (wrapper), Kotlin DSL |
| Package | `com.stealaspawner` |
| Version | `0.1.0-SNAPSHOT` |
| Tests | JUnit 6 + MockBukkit |

## Compiler

Prérequis : un JDK 21 (par exemple Eclipse Temurin 21).

```bash
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows
```

Le plugin est généré dans `build/libs/StealASpawner-<version>.jar`. Copiez-le dans le dossier `plugins/` d'un serveur Paper 1.21.11.

## Documentation

**Wiki utilisateur (anglais)** : [Steal a Spawner — Official Wiki](gitbook/README.md), synchronisé avec GitBook. Règles de maintenance : [docs/DOCUMENTATION.md](docs/DOCUMENTATION.md).

- [Cahier des charges](docs/CAHIER_DES_CHARGES.md) : règles officielles du jeu
- [Roadmap](docs/ROADMAP.md) : phases de développement
- [Architecture](docs/ARCHITECTURE.md) : organisation du code
- [Journal de développement](docs/DEVELOPMENT_LOG.md)
- [Rapports de fin d'étape](docs/rapports/)
- [Changelog](CHANGELOG.md)

## Workflow Git

| Branche | Rôle |
|---|---|
| `main` | Code validé uniquement. Fusion après validation du responsable du projet. |
| `develop` | Intégration du développement en cours. |
| `feature/<nom>` | Nouvelle fonctionnalité, créée depuis `develop`. |
| `fix/<nom>` | Correctif, créé depuis `develop` (ou `main` si urgent). |

Règles :

- Messages de commit au format [Conventional Commits](https://www.conventionalcommits.org/fr/) : `feat:`, `fix:`, `docs:`, `build:`, `ci:`, `test:`, `refactor:`, `chore:`.
- Toute modification passe par une Pull Request ; la CI GitHub Actions compile et teste automatiquement.
- Jamais de `push --force`, jamais de secrets (tokens, mots de passe, IP privées) dans le dépôt.
- Chaque étape met à jour `CHANGELOG.md`, `docs/ROADMAP.md` et `docs/DEVELOPMENT_LOG.md`.
- Toute modification d'une commande, d'une permission ou d'une mécanique met à jour le wiki GitBook dans la même Pull Request ; `./gradlew build` vérifie la documentation (`checkDocs`).
- Chaque étape se termine par un rapport complet dans [`docs/rapports/`](docs/rapports/) (`RAPPORT_PHASE_X.Y.md`).
