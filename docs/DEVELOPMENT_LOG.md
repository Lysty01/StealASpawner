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
