---
description: What Steal a Spawner is and how a game works.
---

# Introduction

## The concept

Steal a Spawner is a competitive multiplayer minigame built around three ideas:

1. **Produce** — every player owns a base where spawners generate money.
2. **Collect** — the money is stored in collectors that the player has to protect.
3. **Steal** — players can raid other bases to steal money, collectors and even spawners.

{% hint style="info" %}
These mechanics are the **official design** of the game. They are 🛠️ **Planned** and not implemented in version `0.1.0-SNAPSHOT`. See [Feature Status](feature-status.md).
{% endhint %}

## Official rules

These rules are fixed by the project's specification and will shape every gameplay feature:

| Rule | Description |
|---|---|
| ♾️ **Unlimited offline production** | Spawners keep producing while their owner is offline, with no time or amount cap. |
| 💰 **100% of money can be stolen** | A raider can take all of a player's unprotected money. |
| 📦 **Collectors can be stolen** | Collectors, and the money they contain, can be stolen. |
| ⬆️ **Stolen spawners keep their level** | A stolen spawner keeps its level and upgrades for its new owner. |

## Technical goals

* **Modular architecture** — each system (economy, spawners, raiding…) is an independent module.
* **LuckPerms support** (planned) — permissions and ranks managed with LuckPerms.
* **Oraxen support** (planned) — custom items and blocks for spawners and collectors.
* **Built for large servers** — designed for around **400 concurrent players**.

## Next steps

* [Install the plugin](installation.md)
* [See what is available today](feature-status.md)
