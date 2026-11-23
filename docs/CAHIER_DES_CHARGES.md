# Cahier des charges — Steal a Spawner

> Ce document fixe les **règles officielles** du jeu. Aucune de ces fonctionnalités n'est encore codée (phase 1.1). Toute évolution des règles doit être validée par le responsable du projet puis consignée ici.

## 1. Concept

Mini-jeu multijoueur sur serveur Paper. Chaque joueur possède une base dans laquelle il installe des spawners. Les spawners produisent de l'argent, stocké dans des collecteurs. Les joueurs peuvent infiltrer les bases adverses pour voler spawners, argent et collecteurs.

## 2. Règles officielles

| # | Règle | Détail |
|---|---|---|
| R1 | **Production hors ligne illimitée** | Les spawners continuent de produire pendant que le joueur est déconnecté, sans plafond de durée ni de montant. La production est calculée à partir du temps écoulé (pas de simulation en continu). |
| R2 | **Vol possible de 100 % de l'argent** | Un voleur peut dérober la totalité de l'argent non sécurisé d'un joueur. Aucun pourcentage protégé par défaut. |
| R3 | **Collecteurs volables** | Les collecteurs (et l'argent qu'ils contiennent) peuvent être volés comme des objets à part entière. |
| R4 | **Niveau des spawners volés conservé** | Un spawner volé garde son niveau et ses améliorations chez son nouveau propriétaire. |

## 3. Exigences techniques

| # | Exigence | Détail |
|---|---|---|
| T1 | **Architecture modulaire** | Chaque domaine (stockage, spawners, économie, vol, factions, intégrations…) est un module indépendant, activable et testable séparément. Voir [ARCHITECTURE.md](ARCHITECTURE.md). |
| T2 | **Compatibilité LuckPerms** (prévue) | Les permissions passent par l'API Bukkit standard, avec intégration LuckPerms optionnelle (groupes, contextes, méta). |
| T3 | **Compatibilité Oraxen** (prévue) | Possibilité d'utiliser des objets et blocs personnalisés Oraxen pour les spawners, collecteurs et interfaces. Dépendance optionnelle. |
| T4 | **Montée en charge : ~400 joueurs** | Aucune opération bloquante (base de données, fichiers, réseau) sur le thread principal ; calculs de production paresseux ; caches ; profilage avant chaque version stable. |
| T5 | **Qualité** | Compilation et tests automatiques à chaque Pull Request ; aucun secret dans le dépôt. |

## 4. Hors périmètre de la phase 1.1

Base de données, spawners, économie, vol, collecteurs, factions, interfaces, intégrations LuckPerms et Oraxen. Ces éléments seront planifiés dans la [roadmap](ROADMAP.md).
