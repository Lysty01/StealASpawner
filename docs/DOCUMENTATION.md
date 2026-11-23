# Documentation utilisateur (GitBook) — règles de maintenance

Le wiki officiel **Steal a Spawner — Official Wiki** est rédigé en anglais dans le dossier [`gitbook/`](../gitbook/README.md) de ce dépôt. Le dépôt GitHub est la **source officielle** : GitBook ne fait qu'afficher son contenu via Git Sync (configuration : [`.gitbook.yaml`](../.gitbook.yaml)).

## Règles permanentes

1. **Une fonctionnalité n'est pas terminée tant que sa documentation utilisateur ou administrateur manque.**
2. Chaque phase du projet comprend une vérification de la documentation GitBook (section dédiée dans le rapport de fin d'étape).
3. Ne jamais documenter comme disponible une fonctionnalité, une commande ou une permission qui n'est pas implémentée. Les éléments prévus portent le badge 🛠️ **Planned**.
4. Ne jamais annoncer le wiki comme publié tant que la connexion GitBook ↔ GitHub n'a pas été configurée et vérifiée.

## À chaque modification d'une commande, d'une permission ou d'une mécanique

Dans **le même commit ou la même Pull Request** que le code :

1. Mettre à jour les pages GitBook concernées.
2. Mettre à jour `gitbook/SUMMARY.md` si la navigation change.
3. Mettre à jour `gitbook/changelog/README.md` (vue joueurs/administrateurs).
4. Mettre à jour `CHANGELOG.md`.
5. Mettre à jour `docs/DEVELOPMENT_LOG.md`.

### Nouvelle commande

- Copier [`docs/templates/COMMAND_TEMPLATE.md`](templates/COMMAND_TEMPLATE.md) vers `gitbook/commands/<commande>.md` et remplir toutes les sections.
- L'ajouter à `gitbook/SUMMARY.md` et au tableau de `gitbook/commands/README.md`.

### Nouvelle permission

- Ajouter une ligne au tableau de `gitbook/permissions/README.md` (permission, fonction, défaut, grade recommandé, commandes, risques).
- La valeur par défaut doit être identique à celle de `plugin.yml`.

## Conventions

| Élément | Convention |
|---|---|
| Langue | Anglais |
| Statut | ✅ **Available** / 🛠️ **Planned** |
| Public | 🟢 **Player** / 🟣 **Premium** / 🔴 **Admin** |
| Encadrés | Blocs GitBook `{% hint style="info / warning / danger / success" %}` |
| Commandes | Toujours dans un bloc de code (copiable) |
| Liens internes | Chemins relatifs vers des fichiers `.md` |

## Contrôle automatique

```bash
./gradlew checkDocs   # contrôle seul
./gradlew build       # compilation + tests + contrôle de la documentation
```

La tâche `checkDocs` ([`gradle/docs-check.gradle.kts`](../gradle/docs-check.gradle.kts)) fait échouer le build, en local comme dans GitHub Actions, si :

- un lien Markdown local (ou une image) pointe vers un fichier inexistant, dans n'importe quel `.md` du dépôt ;
- une page listée dans `gitbook/SUMMARY.md` n'existe pas ;
- une page de `gitbook/` est absente de `SUMMARY.md` ;
- `.gitbook.yaml` pointe vers un dossier, un `README` ou un `SUMMARY` inexistant ;
- une commande déclarée dans `plugin.yml` n'est pas documentée dans `gitbook/commands/` ;
- une permission déclarée dans `plugin.yml` n'est pas documentée dans `gitbook/permissions/README.md`.

## Thème (à configurer dans GitBook)

Le thème ne se règle pas dans le Markdown. Réglages souhaités dans *Customize* de l'espace GitBook :

| Réglage | Valeur souhaitée |
|---|---|
| Titre du site | Steal a Spawner — Official Wiki |
| Mode | Sombre par défaut |
| Couleur principale | Violet (par exemple `#8B5CF6`) |
| Style | Minecraft premium : logo et favicon pixel-art, police lisible pour le texte |
