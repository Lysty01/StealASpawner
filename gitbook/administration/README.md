---
description: Run, configure, update and troubleshoot Steal a Spawner on your server.
---

# Administration

🔴 **Admin** — This section is for server owners and administrators.

## Pages

| Page | Content |
|---|---|
| [Configuration](configuration.md) | Configuration files, loading and validation rules, every `config.yml` setting |
| [Messages](messages.md) | Every message of `messages.yml`, placeholders and formatting |
| [Reserved Files](reserved-files.md) | `spawners.yml`, `economy.yml`, `collectors.yml` |
| [Troubleshooting](troubleshooting.md) | Startup problems and every configuration error |

## Current behaviour (`0.1.0-SNAPSHOT`)

| Topic | Behaviour |
|---|---|
| Configuration | 5 YAML files in `plugins/StealASpawner/`, created on first start — see [Configuration](configuration.md) |
| Reload | [`/sas reload`](../commands/sas-reload.md), applied only if every file is valid |
| Invalid configuration at startup | The plugin refuses to start and disables itself; the files are left untouched |
| Database | None (data storage is 🛠️ Planned) |
| Commands | [`/sas reload`](../commands/sas-reload.md) |
| Permissions | [`stealaspawner.admin.reload`](../permissions/README.md) |
| Gameplay | None yet — see [Feature Status](../getting-started/feature-status.md) |

### Console messages

At startup (first start shown — the `Created default` lines only appear when a file is missing):

```
[StealASpawner] Enabling StealASpawner v0.1.0-SNAPSHOT
[StealASpawner] Created default config.yml
[StealASpawner] Created default messages.yml
[StealASpawner] Created default spawners.yml
[StealASpawner] Created default economy.yml
[StealASpawner] Created default collectors.yml
[StealASpawner] Configuration loaded: 5 files, language=en, log level=INFO (12 ms).
[StealASpawner] StealASpawner 0.1.0-SNAPSHOT enabled.
```

At shutdown:

```
[StealASpawner] Disabling StealASpawner v0.1.0-SNAPSHOT
[StealASpawner] StealASpawner disabled.
```

## Compatibility

| Plugin | Status | Current behaviour |
|---|---|---|
| LuckPerms | 🛠️ Planned integration | Declared as `softdepend`: loaded before Steal a Spawner if installed. Its permissions work like any permission plugin — see [Permissions & Ranks](../permissions/README.md). |
| Oraxen | 🛠️ Planned integration | Declared as `softdepend`: loaded before Steal a Spawner if installed. Not required. |

## Updating

1. Stop the server.
2. **Back up** `plugins/StealASpawner/`.
3. Replace the old `StealASpawner-<version>.jar` in `plugins/` with the new one.
4. Read the [Changelog](../changelog/README.md) for changes that need your attention.
5. Start the server and read the console. Your configuration files are kept; new files are created if needed.

{% hint style="danger" %}
Do not use `/reload` or plugin managers to update Steal a Spawner. Always restart the server. To apply configuration changes only, use [`/sas reload`](../commands/sas-reload.md).
{% endhint %}

## Performance

The plugin is designed for around **400 concurrent players**. Version `0.1.0-SNAPSHOT` runs no repeating task. Configuration files are only read at startup and on `/sas reload`; reading the five small files takes a few milliseconds.

## Reporting a bug

Open an issue on the [GitHub repository](https://github.com/Lysty01/StealASpawner/issues) with your Paper version, Java version, plugin version, the full startup log and the configuration files involved.
