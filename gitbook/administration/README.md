---
description: Run, update and troubleshoot Steal a Spawner on your server.
---

# Administration

🔴 **Admin** — This section is for server owners and administrators.

## Current behaviour (`0.1.0-SNAPSHOT`)

| Topic | Behaviour |
|---|---|
| Configuration file | None |
| Data folder | None is created |
| Database | None (data storage is 🛠️ Planned) |
| Commands | None — see [Commands](../commands/README.md) |
| Permissions | None — see [Permissions & Ranks](../permissions/README.md) |
| Startup | Logs `StealASpawner v<version> activé.` |
| Shutdown | Logs `StealASpawner désactivé.` |

## Compatibility

| Plugin | Status | Current behaviour |
|---|---|---|
| LuckPerms | 🛠️ Planned integration | Declared as `softdepend`: loaded before Steal a Spawner if installed. Not required. |
| Oraxen | 🛠️ Planned integration | Declared as `softdepend`: loaded before Steal a Spawner if installed. Not required. |

## Updating

1. Stop the server.
2. Replace the old `StealASpawner-<version>.jar` in `plugins/` with the new one.
3. Read the [Changelog](../changelog/README.md) for changes that need your attention.
4. Start the server.

{% hint style="danger" %}
Do not use `/reload` or plugin managers to update Steal a Spawner. Always restart the server.
{% endhint %}

## Performance

The plugin is designed for around **400 concurrent players**. Version `0.1.0-SNAPSHOT` runs no task and has no measurable impact on the server.

## Troubleshooting

| Problem | Cause | Solution |
|---|---|---|
| Plugin not listed in `/plugins` | Wrong server software or Java version | Use **Paper 1.21.11** with **Java 21**. Check the console for errors at startup. |
| `UnsupportedClassVersionError` at startup | The server runs on Java 17 or older | Start the server with Java 21. |
| Accents look broken in the console (`activ�`) | The console does not use UTF-8 | Add `-Dstdout.encoding=UTF-8` to your Java start command. |

## Reporting a bug

Open an issue on the [GitHub repository](https://github.com/Lysty01/StealASpawner/issues) with your Paper version, Java version, plugin version and the full startup log.
