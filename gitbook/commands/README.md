---
description: Every Steal a Spawner command, with syntax, permissions and examples.
---

# Commands

The main command is `/stealaspawner`, with the shorter alias `/sas`. Both work everywhere in this wiki.

## Command list

| Command | Audience | Description | Permission | Since |
|---|---|---|---|---|
| [`/sas reload`](sas-reload.md) | 🔴 Admin | Reloads the configuration files without restarting | `stealaspawner.admin.reload` | 0.1.0-SNAPSHOT |

`/sas` without arguments shows the usage to senders who can use a sub-command, and the *no permission* message to everyone else.

{% hint style="info" %}
Only commands that are implemented are listed here. Player commands for spawners, the economy, collectors and raiding will be added when those features are released.
{% endhint %}

## How commands are documented

Every command has its own page, listed in the table above and in the navigation. Every command page contains:

| Section | Content |
|---|---|
| **Name & audience** | Command name and tag: 🟢 Player, 🟣 Premium or 🔴 Admin |
| **Description** | What the command does, exactly |
| **Syntax** | Full syntax, ready to copy |
| **Arguments** | Required `<argument>` and optional `[argument]` parameters |
| **Aliases** | Alternative names |
| **Permission** | Exact permission node and its default value |
| **Who can run it** | Player only, console only, or both |
| **Conditions** | What must be true for the command to work |
| **Cooldown** | Delay between two uses, if any |
| **Examples** | Real examples, ready to copy |
| **Messages** | Success and error messages |
| **Effects** | Exact effects on the player or the server |
| **History** | Version that introduced it and later changes |

### Syntax conventions

| Notation | Meaning |
|---|---|
| `<argument>` | Required argument |
| `[argument]` | Optional argument |
| `a\|b` | Choose one of the values |

In the server console, type commands without the leading `/`.

## Built-in Paper commands

These commands come with Paper, not with Steal a Spawner. They are useful to check that the plugin is installed:

| Command | Effect |
|---|---|
| `/plugins` | Lists installed plugins. `StealASpawner` should appear in green. |
| `/version StealASpawner` | Shows the installed plugin version. |

{% hint style="danger" %}
Do not use Paper's `/reload` command. To apply configuration changes, use [`/sas reload`](sas-reload.md); to update the plugin, restart the server.
{% endhint %}
