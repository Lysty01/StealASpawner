---
description: Every permission node, its default value and the recommended rank.
---

# Permissions & Ranks

{% hint style="info" %}
**Version `0.1.0-SNAPSHOT` has no permissions.**
The plugin does not declare or check any permission yet (its `plugin.yml` declares none). Permission nodes will be added to this page as soon as they exist in the code — never before.
{% endhint %}

## Permission list

| Permission | Function | Default | Recommended rank | Commands | Risk |
|---|---|---|---|---|---|
| *No permissions yet* | — | — | — | — | — |

### Column guide

| Column | Meaning |
|---|---|
| **Permission** | Exact permission node |
| **Function** | What the permission allows |
| **Default** | `true` (everyone), `op` (operators only), `not op` or `false` (nobody) |
| **Recommended rank** | 🟢 Player, 🟣 Premium or 🔴 Admin |
| **Commands** | Commands that require this permission |
| **Risk** | What could go wrong if the permission is given to the wrong people |

## Ranks

Planned ranks used across this wiki:

| Rank | Audience |
|---|---|
| 🟢 **Player** | Every player on the server |
| 🟣 **Premium** | Players with a premium rank |
| 🔴 **Admin** | Staff and server administrators |

## LuckPerms

LuckPerms support is 🛠️ **Planned**. Today, Steal a Spawner only declares LuckPerms as an optional dependency, so LuckPerms (if installed) is loaded first. Ready-to-use LuckPerms commands for each rank will be provided here once permissions exist.
