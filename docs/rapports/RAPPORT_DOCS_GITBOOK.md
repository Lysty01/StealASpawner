# Rapport de développement — Documentation GitBook (structure initiale)

| | |
|---|---|
| **Projet** | Steal a Spawner — plugin Paper |
| **Étape** | Documentation GitBook — initialisation |
| **Date** | 23/11/2026 |
| **Version du plugin** | `0.1.0-SNAPSHOT` (inchangée) |
| **Branche** | `docs/gitbook-init` (depuis `develop`) |
| **Pull Request** | [#2](https://github.com/Lysty01/StealASpawner/pull/2) vers `develop` (non fusionnée) |
| **Statut** | ✅ Terminée — ⏳ en attente de validation |
| **Wiki publié** | ❌ Non : connexion GitBook ↔ GitHub à configurer (§ 9) |

---

## 1. Résumé

Le wiki officiel **Steal a Spawner — Official Wiki** existe dans le dépôt (`gitbook/`), en anglais, prêt pour GitBook Git Sync. Il documente **uniquement ce qui existe** dans la version actuelle : le plugin démarre et s'arrête, sans commande, permission ni configuration. Les mécaniques du cahier des charges sont clairement marquées 🛠️ **Planned**.

Un contrôle automatique (`./gradlew checkDocs`, inclus dans `./gradlew build` et donc dans la CI) bloque toute Pull Request contenant un lien cassé, une page hors navigation, ou une commande ou permission non documentée.

| Indicateur | Résultat |
|---|---|
| Pages GitBook | ✅ 13 pages + `SUMMARY.md` |
| Commandes documentées | 0 (aucune n'est implémentée) |
| Permissions documentées | 0 (aucune n'est déclarée) |
| Contrôle des liens | ✅ 23 fichiers Markdown, 73 liens locaux, 0 erreur |
| Test négatif du contrôle | ✅ 4 erreurs volontaires sur 4 détectées |
| Compilation + tests | ✅ `BUILD SUCCESSFUL`, 3 tests sur 3 |
| Code Java | Non modifié |

---

## 2. Pages créées

| Section | Fichier | Contenu | Statut documenté |
|---|---|---|---|
| Accueil | `gitbook/README.md` | Présentation, liens rapides, légende des badges, source officielle | — |
| Navigation | `gitbook/SUMMARY.md` | Sommaire en 6 groupes | — |
| Getting Started | `getting-started/README.md` | Concept, règles officielles, objectifs techniques | 🛠️ Planned |
| | `getting-started/installation.md` | Prérequis, compilation, installation, vérification par les logs | ✅ Available |
| | `getting-started/feature-status.md` | Tableau Available / Planned | — |
| Economy | `economy/README.md` | Production hors ligne illimitée, vol de 100 % | 🛠️ Planned |
| Spawners | `spawners/README.md` | Production, niveau conservé après un vol | 🛠️ Planned |
| Collectors | `collectors/README.md` | Stockage, collecteurs volables | 🛠️ Planned |
| Factions | `factions/README.md` | Signalé comme prévu et **non spécifié** | 🛠️ Planned |
| Raiding | `raiding/README.md` | Ce qui peut être volé | 🛠️ Planned |
| Commands | `commands/README.md` | « Aucune commande », structure des futures fiches, conventions de syntaxe, commandes Paper utiles | ✅ (vide) |
| Permissions | `permissions/README.md` | « Aucune permission », tableau à 6 colonnes, grades, LuckPerms | ✅ (vide) |
| Administration | `administration/README.md` | Comportement actuel, compatibilité, mise à jour, performances, dépannage | ✅ Available |
| Changelog | `changelog/README.md` | 0.1.0-SNAPSHOT vu par les joueurs et les administrateurs | — |

### Autres fichiers

| Fichier | Rôle |
|---|---|
| `.gitbook.yaml` | Indique à GitBook que le wiki est dans `gitbook/` |
| `gradle/docs-check.gradle.kts` | Tâche `checkDocs` |
| `build.gradle.kts` | Applique `docs-check.gradle.kts` (une ligne ajoutée) |
| `docs/DOCUMENTATION.md` | Règles permanentes de maintenance, conventions, thème souhaité |
| `docs/templates/COMMAND_TEMPLATE.md` | Modèle de fiche commande (les 16 rubriques demandées) |
| `README.md`, `CHANGELOG.md`, `docs/ROADMAP.md`, `docs/ARCHITECTURE.md`, `docs/DEVELOPMENT_LOG.md` | Mis à jour |
| `docs/rapports/RAPPORT_DOCS_GITBOOK.md` | Ce rapport |

---

## 3. Commandes documentées

**Aucune.** Le `plugin.yml` actuel ne déclare aucune commande et le code n'en enregistre aucune. Conformément à la consigne, aucune commande n'est présentée comme disponible.

La page `commands/README.md` est prête à les accueillir : tableau récapitulatif, structure de fiche et conventions de syntaxe. Chaque future commande aura sa fiche, créée à partir de `docs/templates/COMMAND_TEMPLATE.md`, qui couvre : nom, description, syntaxe, arguments, aliases, permission, valeur par défaut, joueur/console, conditions, cooldown, exemples, messages de réussite et d'erreur, effets, version d'introduction et historique.

Seules deux commandes **natives de Paper** sont citées (`/plugins`, `/version StealASpawner`), explicitement présentées comme n'appartenant pas au plugin.

---

## 4. Permissions documentées

**Aucune.** Le `plugin.yml` n'en déclare aucune et le code n'en vérifie aucune. Le tableau `permissions/README.md` comporte les 6 colonnes demandées (permission, fonction, défaut, grade recommandé, commandes, risques) et sera rempli au fur et à mesure.

---

## 5. Design

| Demande | Mise en œuvre dans le Markdown |
|---|---|
| Nom « Steal a Spawner — Official Wiki » | Titre de la page d'accueil et du sommaire |
| Anglais | Toutes les pages GitBook |
| Distinction Player / Premium / Admin | Badges 🟢 **Player**, 🟣 **Premium**, 🔴 **Admin** |
| Disponible / prévu | Badges ✅ **Available**, 🛠️ **Planned**, et encadrés `warning` sur les pages prévues |
| Commandes copiables | Toujours dans des blocs de code (bouton de copie dans GitBook) |
| Tableaux lisibles | Tableaux courts, première colonne en gras |
| Sombre, accents violets, style Minecraft premium | **À configurer dans GitBook** (*Customize*) : valeurs recommandées dans `docs/DOCUMENTATION.md` |

---

## 6. Contrôle automatique de la documentation

`./gradlew checkDocs`, également lancé par `./gradlew build`, donc par GitHub Actions sur chaque Pull Request :

| Contrôle | Résultat actuel |
|---|---|
| Liens Markdown et images locaux de **tous** les `.md` du dépôt | ✅ 73 liens, 0 cassé |
| Pages listées dans `SUMMARY.md` existantes | ✅ |
| Toutes les pages de `gitbook/` présentes dans `SUMMARY.md` | ✅ 13 sur 13 |
| `.gitbook.yaml` cohérent (racine, readme, summary) | ✅ |
| Commandes de `plugin.yml` documentées dans `gitbook/commands/` | ✅ (0 commande) |
| Permissions de `plugin.yml` documentées dans `gitbook/permissions/` | ✅ (0 permission) |

**Test négatif** (fichiers temporaires, retirés ensuite) :

```
Documentation check failed (4 problem(s)):
 - Broken link in gitbook/orphan-test.md: (../nope.md)
 - Page missing from gitbook/SUMMARY.md: gitbook/orphan-test.md
 - Command /sas (plugin.yml) is not documented in gitbook/commands/
 - Permission stealaspawner.test (plugin.yml) is not documented in gitbook/permissions/README.md
```

---

## 7. Compilation et tests

```
./gradlew clean build
> Task :checkDocs
Documentation OK: 23 Markdown files, 73 local links, 13 GitBook pages, 0 commands and 0 permissions checked.
> Task :test
BUILD SUCCESSFUL
```

Tests unitaires : 3 réussis, 0 échec.

---

## 8. Git

| Élément | Détail |
|---|---|
| Branche | `docs/gitbook-init`, créée depuis `develop` (`1709004`, fusion de la PR #1) |
| Commit | `docs: add initial GitBook wiki and documentation checks` |
| Pull Request | [#2](https://github.com/Lysty01/StealASpawner/pull/2) `docs/gitbook-init` → `develop`, créée avec `gh` après connexion du compte Lysty01 (non fusionnée) |
| CI | Workflow *Build & Test* exécuté sur la PR (compilation, tests, `checkDocs`) ; validation locale effectuée |
| Force push | Aucun |

---

## 9. Connecter GitBook au dépôt (à faire par le responsable du projet)

Ces étapes demandent ton compte GitBook et ton compte GitHub : je ne peux pas les faire à ta place.

1. **Créer un compte** sur [gitbook.com](https://www.gitbook.com) (connexion avec GitHub possible).
2. **Créer un espace** (*Space*) nommé **Steal a Spawner — Official Wiki**.
3. Dans l'espace : **Configure → Git Sync** (ou *Integrations → GitHub Sync*).
4. **Autoriser l'application GitBook sur GitHub** et lui donner accès **uniquement** au dépôt `Lysty01/StealASpawner`.
5. Choisir le dépôt `Lysty01/StealASpawner` et la **branche** :
   - `develop` pour suivre le développement en cours,
   - ou `main` pour un wiki qui ne montre que les versions validées (recommandé quand le serveur sera public).
6. ⚠️ **Sens de la première synchronisation : GitHub → GitBook.** Ne pas choisir GitBook → GitHub, sinon l'espace vide écraserait les pages du dépôt.
7. GitBook lit automatiquement `.gitbook.yaml` et prend le dossier `gitbook/` comme racine. Vérifier que les 13 pages et le sommaire apparaissent.
8. **Customize** : mode sombre, couleur principale violette (`#8B5CF6`), logo et favicon.
9. **Publish → Publish to the web** pour rendre le wiki public. Le domaine personnalisé se règle dans les paramètres du site, selon l'offre GitBook.
10. Me prévenir : je vérifierai qu'une modification poussée sur GitHub apparaît bien dans GitBook avant de considérer le wiki comme publié.

> Tant que la PR n'est pas fusionnée, les pages n'existent que sur `docs/gitbook-init`. Connecter GitBook à `develop` après la fusion.

---

## 10. Problèmes rencontrés

| # | Problème | Solution |
|---|---|---|
| 1 | Python absent du poste pour écrire le contrôle des liens. | Contrôle écrit en Kotlin comme tâche Gradle : aucune dépendance supplémentaire, ni en local ni en CI. |
| 2 | Les encadrés GitBook (`{% hint %}`) s'affichent en texte brut sur GitHub. | Comportement normal : ils ne sont rendus que par GitBook. |
| 3 | Le thème (couleurs, mode sombre) ne peut pas être défini dans le Markdown. | Réglages documentés dans `docs/DOCUMENTATION.md`, à appliquer dans GitBook. |
| 4 | Factions et claims absents du cahier des charges. | Page marquée « Planned, non spécifiée », sans règle inventée. |

---

## 11. Vérification de la documentation (règle permanente)

| Question | Réponse |
|---|---|
| Toutes les fonctionnalités disponibles sont-elles documentées ? | ✅ Démarrage/arrêt, `softdepend`, absence de configuration |
| Une fonctionnalité non implémentée est-elle présentée comme disponible ? | ✅ Non |
| Commandes et permissions de `plugin.yml` documentées ? | ✅ (aucune déclarée) |
| `SUMMARY.md` à jour ? | ✅ |
| Liens valides ? | ✅ |

---

## 12. Actions attendues

1. **Relire et valider** la Pull Request `docs/gitbook-init` → `develop`.
2. **Connecter GitBook** en suivant le § 9, après la fusion.
3. Envoyer les instructions de la **phase 1.2**.
