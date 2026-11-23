---
description: Reload the configuration files without restarting the server.
---

# /sas reload

🔴 **Admin** — ✅ **Available** since `0.1.0-SNAPSHOT` (phase 1.2)

Reloads every configuration file of Steal a Spawner (`config.yml`, `messages.yml`, `spawners.yml`, `economy.yml`, `collectors.yml`) without restarting the server. The new configuration is applied only if **every** file is valid; otherwise the current configuration stays active and nothing changes.

## Syntax

```
/sas reload
```

```
/stealaspawner reload
```

## Arguments

None. `/sas reload` with extra arguments is refused with the *invalid syntax* message.

## Details

| | |
|---|---|
| **Aliases** | `/sas` is an alias of `/stealaspawner` |
| **Permission** | `stealaspawner.admin.reload` |
| **Default** | `op` — operators only |
| **Who can run it** | Players with the permission, and the console |
| **Conditions** | The plugin is enabled |
| **Cooldown** | None |
| **Tab completion** | `reload` is suggested only to senders who have the permission |

## Examples

Reload after editing `messages.yml`, from the server console:

```
sas reload
```

In game, as an operator:

```
/sas reload
```

## Messages

Default messages (customizable in [`messages.yml`](../administration/messages.md)), shown after the [prefix](../administration/configuration.md):

| Situation | Key | Default message |
|---|---|---|
| ✅ Success | `reload.success` | `Configuration reloaded (5 files, 3 ms).` |
| ❌ Invalid configuration | `reload.failure` | `Reload failed: 1 error(s). The previous configuration is still active. See the server console for details.` |
| ❌ No permission | `command.no-permission` | `You don't have permission to use this command.` |
| ❌ Extra arguments | `command.invalid-syntax` | `Invalid syntax. Usage: /sas reload` |
| ❌ Unknown sub-command | `command.unknown-subcommand` | `Unknown subcommand: fly. Usage: /sas reload` |
| ℹ️ `/sas` alone | `command.usage` | `Usage: /sas reload` |
| ❌ Unexpected error | `error.internal` | `An internal error occurred. See the server console for details.` |

Senders without the permission receive *no permission* for every `/sas` sub-command, so the command does not reveal its sub-commands.

## Effects

* Missing files are recreated from the defaults. Existing files are only read, never modified.
* **On success**: the new prefix, messages, log level and diagnostics settings apply immediately. The console logs who reloaded and, if `diagnostics.startup-summary` is `true`, a summary line.
* **On failure**: the previous configuration stays fully active — not a single setting changes. Each error is written to the console with its file, path, reason, expected and actual value (see [Troubleshooting](../administration/troubleshooting.md)). The failure message itself still uses the previous prefix and messages.
* Players never see technical details or stack traces.
* Only configuration files are reloaded. No other plugin, world or player data is affected.

## History

| Version | Change |
|---|---|
| 0.1.0-SNAPSHOT (phase 1.2) | Command added |
