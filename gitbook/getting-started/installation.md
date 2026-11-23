---
description: Install Steal a Spawner on a Paper 1.21.11 server.
---

# Installation

## Requirements

| Requirement | Version | Notes |
|---|---|---|
| Server software | **Paper 1.21.11** | Spigot and CraftBukkit are not supported. |
| Java | **21** | Required by Paper 1.21.11. |
| LuckPerms | *Optional* | Loaded before Steal a Spawner if present. No integration yet. |
| Oraxen | *Optional* | Loaded before Steal a Spawner if present. No integration yet. |

## Get the plugin

No official release has been published yet. You can get the plugin in one of two ways.

### Option 1 — Build from source

You need a JDK 21 and Git.

```bash
git clone https://github.com/Lysty01/StealASpawner.git
cd StealASpawner
./gradlew build
```

On Windows, use `gradlew.bat build` instead of `./gradlew build`.

The plugin is created at:

```
build/libs/StealASpawner-0.1.0-SNAPSHOT.jar
```

### Option 2 — Download a CI build

Every pull request is built by GitHub Actions. Open a successful **Build & Test** run in the repository's **Actions** tab and download the `StealASpawner-jar` artifact.

## Install

1. Stop your server.
2. Copy `StealASpawner-<version>.jar` into the server's `plugins/` folder.
3. Start the server.

## Check the installation

On the first start, the plugin creates its configuration files. In the server console you should see:

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

Run the Paper command below in the console. `StealASpawner` must be listed in green:

```
plugins
```

When the server stops, you should see:

```
[StealASpawner] Disabling StealASpawner v0.1.0-SNAPSHOT
[StealASpawner] StealASpawner disabled.
```

{% hint style="warning" %}
If you see `StealASpawner cannot start: N configuration error(s)`, a configuration file is invalid. The errors are listed just above that line. See [Troubleshooting](../administration/troubleshooting.md).
{% endhint %}

## Configuration

The configuration is in `plugins/StealASpawner/`. The defaults work out of the box. To customize the prefix, the messages or the console log level, see [Configuration](../administration/configuration.md), then apply your changes with [`/sas reload`](../commands/sas-reload.md).
