# Changelog

Toutes les modifications notables du projet sont consignées ici.

Le format suit [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/) et le projet utilise le [versionnage sémantique](https://semver.org/lang/fr/).

## [Non publié]

### Ajouté — Phase 1.2 : gestion de la configuration YAML

- Package `com.stealaspawner.config` : `ConfigManager` (chargement transactionnel), `ConfigValidator` (validation stricte, sans I/O), `ConfigurationSnapshot` (état immuable), `ConfigIssue`, `ConfigValidationException`, `ReloadResult`, `ConfigFile`, `LogLevel`, `MessageKey`, `MessageManager`.
- Fichiers `config.yml`, `messages.yml` et fichiers réservés `spawners.yml`, `economy.yml`, `collectors.yml`, copiés au premier démarrage sans jamais écraser un fichier existant.
- Validation : types, champs obligatoires, valeurs autorisées, chaînes vides, version de format, syntaxe YAML (ligne et colonne), fichiers illisibles, cohérence de la langue entre `config.yml` et `messages.yml`. Diagnostics détaillés (fichier, chemin, raison, attendu, obtenu). Clés inconnues : avertissement, ignorées, conservées.
- Configuration invalide au démarrage : le plugin se désactive proprement sans modifier les fichiers.
- Commande `/stealaspawner reload` (alias `/sas reload`), permission `stealaspawner.admin.reload` (défaut : `op`), console autorisée, complétion réservée aux utilisateurs autorisés, aucun détail technique envoyé aux joueurs.
- Rechargement transactionnel : en cas d'erreur, l'ancienne configuration reste active.
- Messages MiniMessage personnalisables, préfixe configurable, placeholders nommés insérés en texte brut (aucune injection de balisage).
- Niveau de journalisation configurable (`WARNING`, `INFO`, `DEBUG`) et résumé de configuration optionnel.
- 47 tests JUnit / MockBukkit (contre 3 auparavant).
- Wiki GitBook : pages Configuration, Messages, Reserved Files, Troubleshooting et `/sas reload` ; pages Commands, Permissions, Administration, Installation, Feature Status, Changelog, Economy, Spawners et Collectors mises à jour.

### Modifié — Phase 1.2

- Messages console de démarrage et d'arrêt en anglais : `StealASpawner <version> enabled.` / `StealASpawner disabled.` (le message de démarrage est personnalisable).

### Corrigé — Synchronisation GitBook

- GitBook Git Sync refusait la branche `develop` : `gitbook-docs.yaml does not exist on this branch.` Ajout de `gitbook-docs.yaml` (configuration du site, validée contre le schéma officiel `https://api.gitbook.com/gitbook-docs.yaml`) : un espace « Official Wiki » associé à la racine du dépôt, où `.gitbook.yaml` pointe déjà vers `gitbook/`. Pages, `SUMMARY.md` et liens inchangés.
- `checkDocs` suit désormais la même résolution que GitBook (`gitbook-docs.yaml` → dossier de l'espace → `.gitbook.yaml` → `SUMMARY.md`) et vérifie la cohérence de `gitbook-docs.yaml`.

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
