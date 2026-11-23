---
description: Customize every message of Steal a Spawner in messages.yml.
---

# Messages

🔴 **Admin** — ✅ **Available** since `0.1.0-SNAPSHOT` (phase 1.2)

Every message the plugin sends is defined in `plugins/StealASpawner/messages.yml`. Edit it, then run [`/sas reload`](../commands/sas-reload.md).

## Format

Messages use the [MiniMessage format](https://docs.advntr.dev/minimessage/format.html):

```yaml
no-permission: "<red>You don't have permission to use this command."
success: "<gradient:#c084fc:#7c3aed>Done!</gradient> <gray>It worked."
```

* The [prefix](configuration.md) from `config.yml` is added before every chat message.
* **Placeholders** such as `<label>` are filled in by the plugin. Each message only supports the placeholders listed below.
* Placeholder values are **always displayed as plain text**. A value such as `<click:run_command:/op me>` is shown literally and can never add colors, clicks or hover text.
* Messages cannot be empty. Quote messages that contain `:` or start with `<`.

## File-level settings

| Path | Type | Default | Accepted values | Required |
|---|---|---|---|---|
| `config-version` | Whole number | `1` | `1` | Yes |
| `language` | Text | `en` | Must match `general.language` in `config.yml` | Yes |

## Message reference

| Key | Default (English) | Placeholders | Used when |
|---|---|---|---|
| `plugin.enabled` | `StealASpawner <version> enabled.` | `<version>` | Logged in the console at startup (formatting is removed in the log) |
| `command.usage` | `Usage: /<label> reload` | `<label>` | `/sas` without arguments, sender allowed to reload |
| `command.no-permission` | `You don't have permission to use this command.` | — | Sender lacks [`stealaspawner.admin.reload`](../permissions/README.md) |
| `command.unknown-subcommand` | `Unknown subcommand: <subcommand>. Usage: /<label> reload` | `<subcommand>`, `<label>` | `/sas <something unknown>` |
| `command.invalid-syntax` | `Invalid syntax. Usage: <usage>` | `<usage>` | `/sas reload` with extra arguments |
| `reload.success` | `Configuration reloaded (<files> files, <duration> ms).` | `<files>`, `<duration>` | Successful reload |
| `reload.failure` | `Reload failed: <errors> error(s). The previous configuration is still active. See the server console for details.` | `<errors>` | Failed reload |
| `error.internal` | `An internal error occurred. See the server console for details.` | — | Unexpected error; details are only written to the console |

`<label>` is the command name the sender typed (`sas` or `stealaspawner`).

## Missing or invalid messages

| Situation | Result |
|---|---|
| A message key is missing | ⚠️ Warning in the console; the built-in English message is used |
| A message is not text (for example a list) | ❌ Error — the configuration is rejected |
| A message is empty | ❌ Error — the configuration is rejected |
| An unknown key is present | ⚠️ Warning; the key is ignored and left in the file |

## Example

```yaml
config-version: 1
language: en

plugin:
  enabled: "StealASpawner <version> enabled."

command:
  usage: "<gray>Try <white>/<label> reload</white>"
  no-permission: "<red>Admins only."
  unknown-subcommand: "<red>No idea what <white><subcommand></white> means. <gray>Try /<label> reload"
  invalid-syntax: "<red>Too many arguments. <gray>Usage: <white><usage></white>"

reload:
  success: "<green>✔ Reloaded <files> files in <duration> ms."
  failure: "<red>✘ <errors> error(s) found — nothing was changed. Check the console."

error:
  internal: "<red>Something went wrong. Please tell an administrator."
```
