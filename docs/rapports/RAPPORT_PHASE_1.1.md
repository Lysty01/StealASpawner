# Rapport de développement — Phase 1.1 : initialisation du projet

| | |
|---|---|
| **Projet** | Steal a Spawner — plugin Paper |
| **Phase** | 1.1 — Initialisation |
| **Date** | 23/11/2026 |
| **Version** | `0.1.0-SNAPSHOT` |
| **Branche** | `feature/phase-1.1-init` (depuis `develop`) |
| **Statut** | ✅ Terminée — ⏳ en attente de validation |
| **Fusion dans `main`** | Non (attend la validation du responsable du projet) |

---

## 1. Résumé

Le squelette du plugin StealASpawner est en place : il compile, ses tests passent et il **démarre et s'arrête proprement sur un vrai serveur Paper 1.21.11**. La documentation, le workflow Git et l'intégration continue sont prêts. Aucune fonctionnalité de jeu n'a été développée, conformément au périmètre de la phase.

| Indicateur | Résultat |
|---|---|
| Compilation | ✅ `BUILD SUCCESSFUL` |
| Tests automatisés | ✅ 3 / 3 réussis |
| Test sur serveur réel | ✅ Activation et arrêt propres, 0 erreur liée au plugin |
| Push GitHub | ✅ `main`, `develop`, `feature/phase-1.1-init` |
| Pull Request | ⚠️ À créer manuellement (`gh` non connecté) |

---

## 2. Objectifs de la phase

| Objectif | Statut |
|---|---|
| Vérifier l'accès au dépôt GitHub et son contenu | ✅ Dépôt privé, vide, accessible |
| Plugin Paper 1.21.11, package `com.stealaspawner`, Gradle Kotlin DSL | ✅ |
| Classe principale `JavaPlugin` avec `onEnable()` / `onDisable()` | ✅ |
| Le plugin compile et démarre sans erreur | ✅ Vérifié sur serveur réel |
| Documentation (README, CHANGELOG, 4 documents `docs/`) | ✅ |
| Règles officielles documentées (non codées) | ✅ Voir [cahier des charges](../CAHIER_DES_CHARGES.md) |
| Branches `main` / `develop` / `feature/*` / `fix/*` | ✅ |
| Conventional Commits | ✅ |
| GitHub Actions sur les Pull Requests | ✅ |
| Aucun force push, aucun secret | ✅ |

---

## 3. Fichiers créés

| Fichier | Rôle |
|---|---|
| `build.gradle.kts` | Configuration du build (Paper API, Java 21, tests) |
| `settings.gradle.kts` | Nom du projet |
| `gradle.properties` | Groupe et version du projet |
| `gradlew`, `gradlew.bat`, `gradle/wrapper/` | Wrapper Gradle 9.9.2 |
| `.gitignore`, `.gitattributes` | Fichiers ignorés, fins de ligne |
| `src/main/java/com/stealaspawner/StealASpawnerPlugin.java` | Point d'entrée du plugin |
| `src/main/resources/plugin.yml` | Descripteur du plugin |
| `src/test/java/com/stealaspawner/StealASpawnerPluginTest.java` | Tests de cycle de vie |
| `.github/workflows/build.yml` | Intégration continue |
| `README.md` | Présentation, compilation, workflow Git |
| `CHANGELOG.md` | Historique des versions |
| `docs/CAHIER_DES_CHARGES.md` | Règles officielles du jeu |
| `docs/ROADMAP.md` | Planning des phases |
| `docs/ARCHITECTURE.md` | Organisation du code |
| `docs/DEVELOPMENT_LOG.md` | Journal de développement |
| `docs/rapports/RAPPORT_PHASE_1.1.md` | Ce rapport |

**Bilan :** 18 fichiers, environ 750 lignes (dont 75 lignes de code et de configuration du plugin).

---

## 4. Architecture

```
StealASpawner/
├── .github/workflows/build.yml
├── build.gradle.kts · settings.gradle.kts · gradle.properties
├── gradlew · gradlew.bat · gradle/wrapper/
├── src/
│   ├── main/java/com/stealaspawner/StealASpawnerPlugin.java
│   ├── main/resources/plugin.yml
│   └── test/java/com/stealaspawner/StealASpawnerPluginTest.java
└── docs/
    ├── CAHIER_DES_CHARGES.md · ROADMAP.md · ARCHITECTURE.md · DEVELOPMENT_LOG.md
    └── rapports/RAPPORT_PHASE_1.1.md
```

- La classe principale est volontairement minimale : elle accueillera les modules des phases suivantes.
- LuckPerms et Oraxen sont déclarés en `softdepend` (optionnels).
- L'architecture modulaire cible est décrite dans [ARCHITECTURE.md](../ARCHITECTURE.md).

---

## 5. Versions utilisées

Toutes les versions ont été vérifiées sur les dépôts officiels le 23/11/2026.

| Composant | Version |
|---|---|
| Java | 21 (Eclipse Temurin 21.0.13) |
| Gradle | 9.9.2 (wrapper, checksum vérifié) |
| Paper API | `1.21.11-R0.1-SNAPSHOT` |
| Serveur de test | Paper 1.21.11 build 132 (STABLE, checksum vérifié) |
| JUnit | 6.1.3 |
| MockBukkit | `mockbukkit-v1.21` 4.116.3 |
| GitHub Actions | checkout v7, setup-java v6, setup-gradle v6, upload-artifact v7 |

---

## 6. Compilation

```
./gradlew clean build
BUILD SUCCESSFUL in 5s
```

Artefact produit : `build/libs/StealASpawner-0.1.0-SNAPSHOT.jar` (1,6 Ko).

---

## 7. Tests effectués

### 7.1 Tests automatisés (MockBukkit)

| Test | Vérifie | Résultat |
|---|---|---|
| `pluginEnablesWithoutError` | Le plugin s'active | ✅ |
| `pluginMetadataIsCorrect` | Nom `StealASpawner`, version `0.1.0-SNAPSHOT` | ✅ |
| `pluginDisablesCleanly` | Le plugin se désactive | ✅ |

**Total : 3 réussis, 0 échec, 0 ignoré.**

### 7.2 Test sur serveur réel

Serveur Paper 1.21.11 build 132, Java 21, EULA acceptée par le responsable du projet.

| Étape | Log observé | Résultat |
|---|---|---|
| Chargement | `Loading server plugin StealASpawner v0.1.0-SNAPSHOT` | ✅ |
| Activation | `StealASpawner v0.1.0-SNAPSHOT activé.` | ✅ |
| Démarrage du serveur | `Done (14.509s)!` | ✅ |
| Commande `/plugins` | `StealASpawner` listé | ✅ |
| Arrêt (`stop`) | `StealASpawner désactivé.` puis sauvegarde des mondes | ✅ |

Aucune erreur ni aucun avertissement liés au plugin.

---

## 8. Git

| Élément | Détail |
|---|---|
| Dépôt | `Lysty01/StealASpawner` (privé) |
| Branches poussées | `main`, `develop`, `feature/phase-1.1-init` |
| Force push | Aucun |

### Commits

| Hash | Branche | Message |
|---|---|---|
| `a24a132` | `main`, `develop` | `chore: initialisation du dépôt` |
| `fb2cfdf` | feature | `build: projet Gradle Kotlin DSL pour Paper 1.21.11 et Java 21` |
| `8ccb30d` | feature | `feat: classe principale StealASpawnerPlugin et plugin.yml` |
| `960abd4` | feature | `test: tests de cycle de vie du plugin avec MockBukkit` |
| `25d8106` | feature | `ci: compilation et tests GitHub Actions sur les Pull Requests` |
| `531bfa3` | feature | `docs: README, changelog, cahier des charges, roadmap, architecture et journal` |
| `8b27067` | feature | `docs: consigner le test de démarrage sur serveur Paper 1.21.11` |
| *(ce rapport)* | feature | `docs: rapport de développement de la phase 1.1` |

---

## 9. Problèmes rencontrés et solutions

| # | Problème | Solution / statut |
|---|---|---|
| 1 | Les tests échouaient : MockBukkit ne peut pas sous-classer une classe `final`. | `final` retiré de la classe principale. ✅ Résolu |
| 2 | Le dossier local contenait des restes d'une tentative précédente (wrapper Gradle, `build/`, `.gradle/`) sans sources. | Rien supprimé. Wrapper conservé après vérification du checksum ; `build/` et `.gradle/` ignorés par Git. ✅ Résolu |
| 3 | Aucune identité Git configurée sur le PC. | Identité locale au dépôt `Lysty01` avec adresse noreply GitHub (adresse personnelle non publiée). ✅ Résolu |
| 4 | `gh` (GitHub CLI) non connecté : impossible de créer la Pull Request automatiquement. | ⚠️ À faire manuellement, voir § 10. |
| 5 | Accents mal affichés dans la console Windows redirigée (`activ�`). | Problème d'encodage de la console, pas du plugin. Option `-Dstdout.encoding=UTF-8` si nécessaire chez l'hébergeur. ℹ️ À surveiller |
| 6 | Paper signale que 1.21.11 a 6 versions de retard sur la dernière stable (26.3). | Cible 1.21.11 conservée conformément au cahier des charges. ℹ️ Décision à confirmer |
| 7 | La CI de la Pull Request #1 échouait : `./gradlew: Permission denied` (code 126). Le dépôt créé sous Windows (`core.filemode=false`) avait enregistré `gradlew` en mode `100644`. | `git update-index --chmod=+x gradlew` (mode `100755` vérifié) + étape `chmod +x ./gradlew` dans le workflow. Aucun code Java modifié. ✅ Résolu (voir § 11) |

---

## 10. Actions attendues du responsable du projet

1. **Relire et valider** la phase 1.1.
2. **Créer la Pull Request** `feature/phase-1.1-init` → `develop` :
   https://github.com/Lysty01/StealASpawner/compare/develop...feature/phase-1.1-init
3. *(Recommandé)* **Protéger la branche `main`** sur GitHub (*Settings > Branches*) pour interdire les push directs.
4. *(Optionnel)* Lancer `gh auth login` pour que les prochaines Pull Requests soient créées automatiquement.
5. **Confirmer la version cible** de Minecraft (1.21.11 ou plus récente).
6. Envoyer les instructions de la **phase suivante**.

---

## 11. Correctif CI (Pull Request #1)

| Élément | Détail |
|---|---|
| Symptôme | `Run ./gradlew build --console=plain` → `./gradlew: Permission denied`, code de sortie 126 |
| Cause | Bit exécutable absent : `gradlew` enregistré en `100644` car Git sous Windows ignore les permissions |
| Correction 1 | `git update-index --chmod=+x gradlew` → `git ls-files -s gradlew` affiche `100755` |
| Correction 2 | Étape `chmod +x ./gradlew` ajoutée dans `.github/workflows/build.yml` avant la compilation |
| Code Java | Non modifié |
| Commit | `fix(ci): correct Gradle wrapper execution permissions` |
| Vérification locale | `./gradlew clean build` : BUILD SUCCESSFUL, 3 tests réussis |

---

*Rapport généré à la fin de la phase 1.1. Un rapport de ce format est produit à la fin de chaque étape dans `docs/rapports/`.*
