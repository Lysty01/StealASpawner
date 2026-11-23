---
description: Version history of Steal a Spawner.
---

# Changelog

All notable changes for players and server owners. The full technical changelog is in the repository: [`CHANGELOG.md`](https://github.com/Lysty01/StealASpawner/blob/develop/CHANGELOG.md).

## 0.1.0-SNAPSHOT — Unreleased

*Development version.*

### Phase 1.2 — Configuration

#### Added

* **Configuration files** in `plugins/StealASpawner/`: `config.yml`, `messages.yml`, and the reserved `spawners.yml`, `economy.yml`, `collectors.yml`. Missing files are created on start; existing files are never overwritten. See [Configuration](../administration/configuration.md).
* **Strict validation** with clear console diagnostics (file, path, reason, expected and actual value). An invalid configuration prevents the plugin from starting instead of running in a broken state.
* **Customizable messages** in MiniMessage format, with a configurable prefix. See [Messages](../administration/messages.md).
* **`/sas reload`** (alias of `/stealaspawner reload`): reloads the configuration safely — if any file is invalid, nothing changes. See [/sas reload](../commands/sas-reload.md).
* **Permission** `stealaspawner.admin.reload` (default: operators). See [Permissions & Ranks](../permissions/README.md).
* **Console log level** (`WARNING`, `INFO`, `DEBUG`) and an optional configuration summary.

#### Changed

* Startup and shutdown console messages are now in English: `StealASpawner <version> enabled.` and `StealASpawner disabled.`

### Phase 1.1 — Initialisation

#### Added

* The plugin installs, enables and disables cleanly on **Paper 1.21.11** (Java 21).
* LuckPerms and Oraxen declared as optional dependencies (`softdepend`).

### Not included yet

* No gameplay feature. See [Feature Status](../getting-started/feature-status.md).
