# Roadmap — Steal a Spawner

Légende : ✅ terminé · 🔄 en cours / en attente de validation · ⏳ à venir

## Phase 1 — Fondations

| Étape | Contenu | Statut |
|---|---|---|
| 1.1 | Initialisation : projet Gradle, classe principale, documentation, workflow Git, CI | ✅ Fusionnée dans `develop` (PR #1) |
| Docs | Wiki GitBook officiel (structure initiale) et contrôle automatique de la documentation | ✅ Fusionnée dans `develop` (PR #2, #3) |
| 1.2 | Gestion de la configuration YAML : chargement, validation stricte, reload transactionnel, messages, `/sas reload` | 🔄 En attente de validation |
| 1.3+ | Étapes suivantes à définir par le responsable du projet | ⏳ |

## Phases ultérieures (périmètre indicatif, non planifié)

Les domaines ci-dessous découlent du [cahier des charges](CAHIER_DES_CHARGES.md). Leur découpage et leur ordre seront décidés au fur et à mesure ; rien n'est développé avant validation explicite.

Chaque phase inclut la mise à jour et la vérification du wiki GitBook : une fonctionnalité sans documentation utilisateur ou administrateur n'est pas considérée comme terminée (voir [DOCUMENTATION.md](DOCUMENTATION.md)).

- Stockage des données (base de données)
- Spawners et niveaux
- Économie et production hors ligne
- Collecteurs
- Vol (argent, spawners, collecteurs)
- Factions
- Intégrations LuckPerms et Oraxen
- Optimisation et tests de charge (~400 joueurs)
