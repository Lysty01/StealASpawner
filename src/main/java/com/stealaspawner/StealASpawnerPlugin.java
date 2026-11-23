package com.stealaspawner;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Point d'entrée du plugin Steal a Spawner.
 *
 * <p>Phase 1.1 : squelette uniquement. Les modules (stockage, spawners, économie,
 * vol, factions…) seront branchés ici lors des phases suivantes.</p>
 */
public class StealASpawnerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("StealASpawner v" + getPluginMeta().getVersion() + " activé.");
    }

    @Override
    public void onDisable() {
        getLogger().info("StealASpawner désactivé.");
    }
}
