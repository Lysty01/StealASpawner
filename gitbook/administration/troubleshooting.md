---
description: Fix startup problems and every configuration error reported by Steal a Spawner.
---

# Troubleshooting

🔴 **Admin**

## Startup problems

| Problem | Cause | Solution |
|---|---|---|
| Plugin not listed in `/plugins` | Wrong server software or Java version | Use **Paper 1.21.11** with **Java 21**. Check the console for errors at startup. |
| `UnsupportedClassVersionError` at startup | The server runs on Java 17 or older | Start the server with Java 21. |
| `StealASpawner cannot start: N configuration error(s)` | A configuration file is invalid | Read the errors printed just above, fix the files, then restart. The plugin stays disabled until then. |
| `Cannot execute command 'sas' in plugin StealASpawner ... - plugin is disabled` | The plugin did not start (usually an invalid configuration) | `/sas reload` cannot work while the plugin is disabled. Fix the errors shown at startup, then **restart** the server. |
| Special characters look broken in the console (for example `�`) | The console does not use UTF-8 | Add `-Dstdout.encoding=UTF-8` to your Java start command. |

## Reading a configuration error

Every problem is printed with the same layout:

```
[StealASpawner] Configuration error:
File: config.yml
Path: logging.level
Reason: unsupported log level
Expected: WARNING, INFO, DEBUG
Actual: VERBOSE
```

| Line | Meaning |
|---|---|
| `File` | File in `plugins/StealASpawner/` |
| `Path` | Setting, written with dots: `logging.level` is `level` inside `logging:` (absent when the whole file is concerned) |
| `Reason` | What is wrong |
| `Expected` | What the plugin accepts |
| `Actual` | What it found |

`Configuration warning:` blocks use the same layout. Warnings never prevent loading.

## Configuration errors

| Reason | Cause | Fix |
|---|---|---|
| `invalid YAML syntax at line L, column C` | The file is not valid YAML | See [YAML syntax errors](#yaml-syntax-errors) below. |
| `wrong value type` | For example `startup-summary: maybe` instead of `true`/`false` | Use the type shown in `Expected`. |
| `required value is missing` | A required setting was deleted | Add it back (see the [configuration reference](configuration.md#settings)). |
| `required value is empty` / `message is empty` | A required text is `""` | Write a value. |
| `unsupported language` | `general.language` is not `en` | Use `en`. |
| `unsupported log level` | `logging.level` is not `WARNING`, `INFO` or `DEBUG` | Use one of these values. |
| `unsupported configuration version` | `config-version` was changed | Set it back to `1`. |
| `messages.yml language does not match general.language in config.yml` | The two `language` values differ | Use the same value in both files. |
| `file is not valid UTF-8 text` | The file was saved in another encoding | Save it as UTF-8. |
| `file cannot be read` / `not a regular file` | Permissions, or a folder with the file's name | Check the file and its permissions. |
| `file is missing and cannot be created` | The server cannot write to `plugins/StealASpawner/` | Check the folder permissions. |

## Configuration warnings

| Reason | Effect | What to do |
|---|---|---|
| `unknown key, ignored` | The key is ignored and left in the file | Check for a typo. In reserved files, settings have no effect yet. |
| `value is missing, using the default value` | The default value is used | Add the setting to silence the warning. |
| `message is missing, using the built-in default` | The English default message is used | Add the message back to `messages.yml`. |

## YAML syntax errors

Common causes:

* **Tabs** used for indentation — YAML only accepts spaces.
* **Inconsistent indentation** — children of a section must all use the same number of spaces.
* A **missing space after `:`** (`level:INFO` instead of `level: INFO`).
* **Unclosed quotes or brackets**.
* Text starting with `<`, `[`, `{`, `&`, `*`, `!`, `%`, `@` or containing `: ` that is **not quoted**. Always quote messages and the prefix.

The line and column in the error point where the YAML parser gave up; the real mistake is often on that line or just above it.

## After fixing

* **At startup**: restart the server.
* **While running**: run [`/sas reload`](../commands/sas-reload.md). If it fails, the previous configuration is still active and the errors are in the console.

## Reporting a bug

Open an issue on the [GitHub repository](https://github.com/Lysty01/StealASpawner/issues) with your Paper version, Java version, plugin version, the full startup log and the configuration files involved.
