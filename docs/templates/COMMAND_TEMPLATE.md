<!--
GitBook command page template.
Copy to gitbook/commands/<command-name>.md, fill every section, then:
  - add the page to gitbook/SUMMARY.md (under "Commands"),
  - add a row to the table in gitbook/commands/README.md,
  - document its permission in gitbook/permissions/README.md,
  - run ./gradlew check.
Never create this page before the command is implemented.
-->

---
description: <One-line description of the command.>
---

# /<command>

🟢 **Player** <!-- or 🟣 **Premium** / 🔴 **Admin** -->

<Exact description of what the command does.>

## Syntax

```
/<command> <required> [optional]
```

## Arguments

| Argument | Required | Type | Description |
|---|---|---|---|
| `<required>` | Yes | <type> | <description> |
| `[optional]` | No | <type> | <description, default value> |

## Details

| | |
|---|---|
| **Aliases** | `/<alias>` or *None* |
| **Permission** | `stealaspawner.<node>` |
| **Default** | `true` / `op` / `false` |
| **Who can run it** | Players only / Console only / Players and console |
| **Conditions** | <what must be true> |
| **Cooldown** | <duration> or *None* |

## Examples

```
/<command> <example>
```

<What happens.>

## Messages

| Situation | Message |
|---|---|
| ✅ Success | `<exact message>` |
| ❌ No permission | `<exact message>` |
| ❌ Wrong usage | `<exact message>` |
| ❌ <other error> | `<exact message>` |

## Effects

* <Exact effect on the player>
* <Exact effect on the server or stored data>

## History

| Version | Change |
|---|---|
| <x.y.z> | Command added |
