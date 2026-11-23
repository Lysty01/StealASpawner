---
description: spawners.yml, economy.yml and collectors.yml — reserved for features that are not released yet.
---

# Reserved Files

🔴 **Admin** — 🛠️ **Reserved**

`spawners.yml`, `economy.yml` and `collectors.yml` are created now so that the configuration folder already has its final layout. **They contain no gameplay setting yet**, because spawners, the economy and collectors are not implemented.

{% hint style="warning" %}
Adding settings to these files has **no effect**. Unknown keys are reported as warnings in the console and ignored (they are left in the file). Settings will be added — and documented here — when each feature is released.
{% endhint %}

## Content

Each file only contains its format version:

```yaml
# Format version of this file. Do not change it.
config-version: 1
```

| Path | Type | Default | Accepted values | Required |
|---|---|---|---|---|
| `config-version` | Whole number | `1` | `1` | Yes |

## Official rules

These rules are fixed by the project specification. They will apply when the features are released and **will not be configurable**:

| File | Feature | Official rule |
|---|---|---|
| `spawners.yml` | [Spawners](../spawners/README.md) | Spawners keep producing while their owner is offline, **with no limit**. A stolen spawner **keeps its level** and upgrades. |
| `economy.yml` | [Economy](../economy/README.md) | **100%** of a player's accumulated money can be stolen. |
| `collectors.yml` | [Collectors](../collectors/README.md) | Collectors, and the money they contain, **can be stolen**. |
