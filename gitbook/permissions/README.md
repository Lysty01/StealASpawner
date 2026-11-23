---
description: Every permission node, its default value and the recommended rank.
---

# Permissions & Ranks

## Permission list

| Permission | Function | Default | Recommended rank | Commands | Risk |
|---|---|---|---|---|---|
| `stealaspawner.admin.reload` | Reload the plugin configuration files | `op` | 🔴 Admin | [`/sas reload`](../commands/sas-reload.md) | Low. Allows applying configuration changes, including the messages every player sees. A broken file is rejected and the previous configuration is kept, so it cannot break the plugin. Give it only to staff who are allowed to edit the configuration. |

The console always has every permission.

{% hint style="info" %}
Only permissions that exist in the plugin are listed here. New permissions are added in the same release as the feature that uses them.
{% endhint %}

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

Ranks used across this wiki:

| Rank | Audience | Permissions today |
|---|---|---|
| 🟢 **Player** | Every player on the server | None |
| 🟣 **Premium** | Players with a premium rank | None |
| 🔴 **Admin** | Staff and server administrators | `stealaspawner.admin.reload` |

## LuckPerms

Steal a Spawner uses standard Bukkit permissions, so any permission plugin works. With [LuckPerms](https://luckperms.net/), give the reload permission to an `admin` group:

```
lp group admin permission set stealaspawner.admin.reload true
```

To a single player:

```
lp user <player> permission set stealaspawner.admin.reload true
```

A deeper LuckPerms integration (contexts, meta) is 🛠️ **Planned**.
