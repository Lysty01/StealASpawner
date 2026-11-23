package com.stealaspawner;

import com.stealaspawner.command.StealASpawnerCommand;
import com.stealaspawner.config.ConfigManager;
import com.stealaspawner.config.ConfigValidationException;
import com.stealaspawner.config.MessageKey;
import com.stealaspawner.config.MessageManager;
import com.stealaspawner.logging.PluginLog;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import static com.stealaspawner.config.MessageManager.placeholder;

/**
 * Entry point of Steal a Spawner. Only wires the modules together; no gameplay logic lives here.
 */
public class StealASpawnerPlugin extends JavaPlugin {

    private PluginLog log;
    private ConfigManager configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        log = new PluginLog(getLogger());
        configManager = new ConfigManager(getDataFolder().toPath(), this::getResource, log);
        try {
            configManager.load();
        } catch (ConfigValidationException e) {
            log.severe("StealASpawner cannot start: " + e.errors().size()
                    + " configuration error(s), listed above. Fix the files, then restart the server.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (RuntimeException e) {
            log.severe("StealASpawner cannot start: unexpected error while loading the configuration.", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        messageManager = new MessageManager(configManager::snapshot, log);
        registerCommands();
        log.info(messageManager.plain(MessageKey.PLUGIN_ENABLED, placeholder("version", getPluginMeta().getVersion())));
    }

    @Override
    public void onDisable() {
        if (configManager != null) {
            configManager.shutdown();
        }
        if (log != null) {
            log.info("StealASpawner disabled.");
        }
    }

    private void registerCommands() {
        PluginCommand command = getCommand("stealaspawner");
        if (command == null) {
            throw new IllegalStateException("Command 'stealaspawner' is missing from plugin.yml");
        }
        StealASpawnerCommand executor = new StealASpawnerCommand(configManager, messageManager, log);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    /** Configuration of the plugin, for the other modules. Only valid while the plugin is enabled. */
    public ConfigManager configManager() {
        return configManager;
    }

    /** Message renderer, for the other modules. Only valid while the plugin is enabled. */
    public MessageManager messageManager() {
        return messageManager;
    }
}
