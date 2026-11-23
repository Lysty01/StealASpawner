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

In the server console you should see:

```
[StealASpawner] Loading server plugin StealASpawner v0.1.0-SNAPSHOT
[StealASpawner] Enabling StealASpawner v0.1.0-SNAPSHOT
[StealASpawner] StealASpawner v0.1.0-SNAPSHOT activé.
```

Run the Paper command below in the console. `StealASpawner` must be listed in green:

```
plugins
```

When the server stops, you should see:

```
[StealASpawner] Disabling StealASpawner v0.1.0-SNAPSHOT
[StealASpawner] StealASpawner désactivé.
```

{% hint style="info" %}
The current log messages are in French. If accents look broken in your console (for example `activ�`), see [Administration › Troubleshooting](../administration/README.md#troubleshooting).
{% endhint %}

## Configuration

Version `0.1.0-SNAPSHOT` has **no configuration file** and creates no data folder.
