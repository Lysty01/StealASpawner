---
description: The configuration files of Steal a Spawner, how they are loaded and every setting of config.yml.
---

# Configuration

🔴 **Admin** — ✅ **Available** since `0.1.0-SNAPSHOT` (phase 1.2)

## Files

On first start, Steal a Spawner creates its configuration in `plugins/StealASpawner/`:

```
plugins/StealASpawner/
├── config.yml       General settings
├── messages.yml     Player and console messages
├── spawners.yml     Reserved for spawners (no setting yet)
├── economy.yml      Reserved for the economy (no setting yet)
└── collectors.yml   Reserved for collectors (no setting yet)
```

| File | Status | Page |
|---|---|---|
| `config.yml` | ✅ Available | This page |
| `messages.yml` | ✅ Available | [Messages](messages.md) |
| `spawners.yml`, `economy.yml`, `collectors.yml` | 🛠️ Reserved — contain no gameplay setting | [Reserved Files](reserved-files.md) |

All files are UTF-8 YAML. Every file starts with `config-version: 1`: this is the format version of the file. **Do not change it.**

## How the configuration is loaded

1. **Missing files are created** from the defaults bundled in the plugin. Existing files are **never overwritten, rewritten or deleted** — the plugin only reads them, so your comments and layout are kept.
2. **Every file is read and validated** before anything is applied.
3. If everything is valid, the new configuration is applied **all at once**.
4. If there is any error:
   * **at startup**, the plugin refuses to start and disables itself, so it never runs with a broken configuration;
   * **on reload**, the previous configuration stays active (see [`/sas reload`](../commands/sas-reload.md)).

Every problem is written to the server console with the file, the path, the reason, the expected value and the actual value:

```
[StealASpawner] Configuration error:
File: config.yml
Path: general.language
Reason: unsupported language
Expected: en
Actual: xx
```

All errors are reported at once, so you can fix everything in one go. See [Troubleshooting](troubleshooting.md) for every possible message.

### Validation rules

| Situation | Result |
|---|---|
| Invalid YAML syntax | ❌ Error (with line and column) |
| Wrong value type (for example text instead of `true`/`false`) | ❌ Error |
| Unsupported value (for example `language: xx`) | ❌ Error |
| Required value missing or empty | ❌ Error |
| Unsupported `config-version` | ❌ Error |
| Optional value missing | ⚠️ Warning — the default value is used |
| Message missing from `messages.yml` | ⚠️ Warning — the built-in English message is used |
| Unknown key (typo, setting from another plugin…) | ⚠️ Warning — the key is ignored and **left in the file** |
| File missing | Created from the default |

{% hint style="warning" %}
An invalid value is **never** silently replaced by a default value. Fix the file, then run `/sas reload`.
{% endhint %}

## `config.yml` reference

### Default file

```yaml
# Format version of this file. Do not change it.
config-version: 1

general:
  prefix: "<dark_gray>[<gradient:#c084fc:#7c3aed>StealASpawner</gradient>]</dark_gray> "
  language: en

logging:
  level: INFO

diagnostics:
  startup-summary: true
```

### Settings

| Path | Type | Default | Accepted values | Required | Reloadable |
|---|---|---|---|---|---|
| `config-version` | Whole number | `1` | `1` | Yes | — |
| `general.prefix` | Text (MiniMessage) | Purple `[StealASpawner]` | Any text; `""` disables the prefix | No | ✅ |
| `general.language` | Text | `en` | `en` | Yes | ✅ |
| `logging.level` | Text | `INFO` | `WARNING`, `INFO`, `DEBUG` (any case) | No | ✅ |
| `diagnostics.startup-summary` | Boolean | `true` | `true`, `false` | No | ✅ |

#### `general.prefix`

Text shown before every chat message the plugin sends to players and to the console. It uses the [MiniMessage format](https://docs.advntr.dev/minimessage/format.html) (colors, gradients, bold…). Set it to `""` to send messages without a prefix. It is not added to console log lines.

#### `general.language`

Language of the plugin messages. Only `en` (English) is available for now. It must match the `language` value of [`messages.yml`](messages.md); otherwise the configuration is rejected.

#### `logging.level`

Controls what Steal a Spawner writes to the server console.

| Value | Effect |
|---|---|
| `WARNING` | Only warnings and errors. Startup and reload information is hidden. |
| `INFO` | Normal operation messages. **Recommended.** |
| `DEBUG` | `INFO` plus detailed diagnostic lines prefixed with `[DEBUG]` (for example, each file read). Use it to investigate a problem, then switch back to `INFO`. |

#### `diagnostics.startup-summary`

When `true`, a one-line summary is logged after startup and after each successful reload:

```
[StealASpawner] Configuration loaded: 5 files, language=en, log level=INFO (4 ms).
```

## Examples

### Custom prefix and quieter console

```yaml
config-version: 1

general:
  prefix: "<gold><bold>SaS</bold></gold> <dark_gray>»</dark_gray> "
  language: en

logging:
  level: WARNING

diagnostics:
  startup-summary: false
```

### No prefix, debug logging

```yaml
config-version: 1

general:
  prefix: ""
  language: en

logging:
  level: DEBUG

diagnostics:
  startup-summary: true
```

## Best practices

* **Back up** `plugins/StealASpawner/` before editing.
* Edit with a text editor that saves in **UTF-8** and uses **spaces, never tabs**, for indentation.
* After editing, run [`/sas reload`](../commands/sas-reload.md) and **read the console**. A failed reload keeps the previous configuration, so a mistake never breaks a running server.
* Keep `logging.level: INFO` in production. Use `DEBUG` only while investigating.
* Do not change `config-version`.
* To get a fresh default file, rename or delete it and run `/sas reload` (or restart): it will be recreated.
