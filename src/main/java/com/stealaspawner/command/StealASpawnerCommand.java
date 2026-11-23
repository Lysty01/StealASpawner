package com.stealaspawner.command;

import com.stealaspawner.config.ConfigManager;
import com.stealaspawner.config.MessageKey;
import com.stealaspawner.config.MessageManager;
import com.stealaspawner.config.ReloadResult;
import com.stealaspawner.logging.PluginLog;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.stealaspawner.config.MessageManager.placeholder;

/**
 * {@code /stealaspawner} (alias {@code /sas}).
 *
 * <p>Sub-commands: {@code reload} (permission {@value #RELOAD_PERMISSION}).</p>
 */
public final class StealASpawnerCommand implements TabExecutor {

    public static final String RELOAD_PERMISSION = "stealaspawner.admin.reload";
    private static final String RELOAD = "reload";

    private final ConfigManager configManager;
    private final MessageManager messages;
    private final PluginLog log;

    public StealASpawnerCommand(ConfigManager configManager, MessageManager messages, PluginLog log) {
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        this.messages = Objects.requireNonNull(messages, "messages");
        this.log = Objects.requireNonNull(log, "log");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            execute(sender, label, args);
        } catch (RuntimeException e) {
            log.severe("Unexpected error while running /" + label + " for " + sender.getName(), e);
            try {
                messages.send(sender, MessageKey.ERROR_INTERNAL);
            } catch (RuntimeException ignored) {
                // The error is already logged; never expose details to the sender.
            }
        }
        return true;
    }

    private void execute(CommandSender sender, String label, String[] args) {
        boolean canReload = sender.hasPermission(RELOAD_PERMISSION);
        if (args.length == 0) {
            messages.send(sender, canReload ? MessageKey.COMMAND_USAGE : MessageKey.COMMAND_NO_PERMISSION,
                    placeholder("label", label));
            return;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (!canReload) {
            messages.send(sender, MessageKey.COMMAND_NO_PERMISSION);
            return;
        }
        if (!sub.equals(RELOAD)) {
            messages.send(sender, MessageKey.COMMAND_UNKNOWN_SUBCOMMAND,
                    placeholder("subcommand", args[0]), placeholder("label", label));
            return;
        }
        if (args.length > 1) {
            messages.send(sender, MessageKey.COMMAND_INVALID_SYNTAX, placeholder("usage", "/" + label + " " + RELOAD));
            return;
        }
        reload(sender);
    }

    private void reload(CommandSender sender) {
        log.info(sender.getName() + " requested a configuration reload.");
        ReloadResult result = configManager.reload();
        if (result.success()) {
            messages.send(sender, MessageKey.RELOAD_SUCCESS,
                    placeholder("files", result.fileCount()), placeholder("duration", result.duration().toMillis()));
        } else {
            messages.send(sender, MessageKey.RELOAD_FAILURE, placeholder("errors", result.errors().size()));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(RELOAD_PERMISSION)
                && RELOAD.startsWith(args[0].toLowerCase(Locale.ROOT))) {
            return List.of(RELOAD);
        }
        return List.of();
    }
}
