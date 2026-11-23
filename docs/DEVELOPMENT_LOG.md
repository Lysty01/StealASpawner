# Journal de développement — Steal a Spawner

Une entrée par étape : objectif, réalisation, vérifications, problèmes rencontrés.

---

## 2026-11-23 — Phase 1.1 : initialisation du projet

**Branche :** `feature/phase-1.1-init` (depuis `develop`)

### Objectif

Créer le squelette du plugin Paper 1.21.11, la documentation, le workflow Git et la CI, sans aucune fonctionnalité de jeu.

### Versions vérifiées (dépôts officiels, 23/11/2026)

| Outil | Version | Source |
|---|---|---|
| Paper API | `1.21.11-R0.1-SNAPSHOT` | repo.papermc.io |
| Java | 21 (Temurin 21.0.13 en local) | exigence Paper 1.21.11 |
| Gradle | 9.9.2 (version courante) | services.gradle.org |
| JUnit | 6.1.3 (dernière version stable) | Maven Central |
| MockBukkit | `mockbukkit-v1.21` 4.116.3 | Maven Central |

### Réalisé

- Projet Gradle Kotlin DSL + wrapper 9.9.2 (checksum du `gradle-wrapper.jar` vérifié contre celui publié par Gradle).
- `StealASpawnerPlugin` avec `onEnable()` / `onDisable()` (simples logs).
- `plugin.yml` avec `softdepend: [LuckPerms, Oraxen]`.
- 3 tests MockBukkit : activation, métadonnées (nom, version), désactivation.
- Documentation complète et CI GitHub Actions.

### Vérifications

- `./gradlew clean build` : **BUILD SUCCESSFUL**.
- Tests : 3 exécutés, 0 échec.
- Test réel sur serveur **Paper 1.21.11 build 132** (canal STABLE, checksum SHA-256 vérifié), Java 21 :
  - chargement, activation (`StealASpawner v0.1.0-SNAPSHOT activé.`) et présence dans `/plugins` ;
  - serveur prêt en 14,5 s ;
  - `stop` : désactivation propre (`StealASpawner désactivé.`) puis sauvegarde des mondes ;
  - aucune erreur ni aucun avertissement liés au plugin.

### Problèmes rencontrés

- MockBukkit ne peut pas charger une classe principale `final` (il la sous-classe) : le modificateur `final` a été retiré.
- Le dossier local contenait déjà un wrapper Gradle et des dossiers `build/` / `.gradle/` issus d'une tentative précédente, sans sources. Rien n'a été supprimé : le wrapper (authentique) a été conservé, `build/` et `.gradle/` sont ignorés par Git.
- Lors du test serveur, les accents des logs s'affichaient mal (`activ�`) dans la console Windows redirigée : il s'agit d'un problème d'encodage de la console, pas du plugin. À surveiller selon l'hébergeur (lancer Java avec `-Dstdout.encoding=UTF-8` au besoin).
- **CI en échec sur la Pull Request #1** : `./gradlew: Permission denied` (code 126). Cause : le dépôt a été créé sous Windows, où Git ignore les permissions (`core.filemode=false`) ; `gradlew` a donc été enregistré en mode `100644` (non exécutable) et le runner Linux de GitHub Actions refusait de l'exécuter. Correction : `git update-index --chmod=+x gradlew` (mode `100755` vérifié avec `git ls-files -s`) et ajout d'une étape `chmod +x ./gradlew` dans le workflow, par sécurité. Aucun code Java modifié.
- Paper signale que 1.21.11 a 6 versions de retard sur la dernière stable (26.3). La cible 1.21.11 reste celle définie par le cahier des charges.

---

## 2026-11-23 — Documentation GitBook : structure initiale

**Branche :** `docs/gitbook-init` (depuis `develop`, après fusion de la PR #1)

### Objectif

Créer le wiki officiel *Steal a Spawner — Official Wiki* (anglais), synchronisable avec GitHub, documentant uniquement ce qui existe réellement, et mettre en place un contrôle automatique de la documentation. Aucun code Java modifié.

### Réalisé

- Dossier `gitbook/` : 13 pages + `SUMMARY.md`, réparties dans les 11 sections demandées.
- `.gitbook.yaml` (racine `./gitbook/`) pour GitBook Git Sync.
- État réel documenté : le plugin démarre et s'arrête, **aucune commande, aucune permission, aucun fichier de configuration**. Les mécaniques du cahier des charges sont marquées 🛠️ **Planned** ; les factions sont signalées comme non spécifiées.
- Conventions visuelles : ✅ Available / 🛠️ Planned, 🟢 Player / 🟣 Premium / 🔴 Admin, encadrés GitBook, commandes en blocs de code.
- Tâche Gradle `checkDocs` branchée sur `check` (donc exécutée par la CI existante).
- `docs/DOCUMENTATION.md` (règles de maintenance) et `docs/templates/COMMAND_TEMPLATE.md`.

### Vérifications

- `./gradlew checkDocs` : 21 fichiers Markdown, 66 liens locaux, 13 pages GitBook, 0 commande, 0 permission → OK.
- Test négatif : un lien cassé, une page hors `SUMMARY.md`, une commande et une permission non documentées ajoutés temporairement → les 4 problèmes sont détectés et le build échoue. Fichiers de test retirés ensuite.

### Problèmes rencontrés

- Python absent du poste : le contrôle des liens a été écrit en Kotlin (tâche Gradle), ce qui évite toute dépendance supplémentaire en local comme en CI.
- Le wiki n'est **pas publié** : la connexion GitBook ↔ GitHub doit être faite par le responsable du projet depuis son compte GitBook.

---

## 2026-11-23 — Correctif : synchronisation GitBook

**Branche :** `fix/gitbook-sync` (depuis `develop`, après fusion de la PR #2)

### Problème

Git Sync (app.gitbook.com) reconnaissait le dépôt et la branche `develop`, mais affichait `gitbook-docs.yaml does not exist on this branch.`

### Analyse

- Documentation officielle *Content configuration* et schéma `https://api.gitbook.com/gitbook-docs.yaml` consultés : Git Sync utilise désormais `gitbook-docs.yaml` (site → espaces → dossiers), puis `.gitbook.yaml` (configuration d'un espace), puis `SUMMARY.md`.
- Un espace peut être placé directement à la racine de `site.structure` (site sans section), avec un seul espace `default: true`.

### Réalisé

- `gitbook-docs.yaml` : site « Steal a Spawner — Official Wiki », espace `space-official-wiki` associé à `./`. Le `.gitbook.yaml` existant (`root: ./gitbook/`) s'applique sans modification.
- `checkDocs` : résolution identique à celle de GitBook et contrôles de cohérence de `gitbook-docs.yaml`.
- `docs/DOCUMENTATION.md`, `docs/ARCHITECTURE.md`, `CHANGELOG.md` et le rapport GitBook (§ 13) mis à jour.

### Vérifications

- `gitbook-docs.yaml` **valide** contre le schéma officiel (validateur JSON Schema 2020-12, projet jetable hors dépôt) ; deux contre-exemples rejetés.
- Tests négatifs de `checkDocs` : 4 scénarios, toutes les erreurs détectées.
- `./gradlew clean build` : BUILD SUCCESSFUL, 3 tests sur 3. Aucune page, aucun lien et aucun code Java modifiés.

### Problèmes rencontrés

- Ni Python ni Node.js sur le poste pour valider le schéma : validation faite avec une bibliothèque Java dans un projet temporaire, sans ajouter de dépendance au projet.
