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

### Problèmes rencontrés

- MockBukkit ne peut pas charger une classe principale `final` (il la sous-classe) : le modificateur `final` a été retiré.
- Le dossier local contenait déjà un wrapper Gradle et des dossiers `build/` / `.gradle/` issus d'une tentative précédente, sans sources. Rien n'a été supprimé : le wrapper (authentique) a été conservé, `build/` et `.gradle/` sont ignorés par Git.
