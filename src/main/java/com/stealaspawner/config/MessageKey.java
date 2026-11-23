package com.stealaspawner.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Stable keys of {@code messages.yml}, with the placeholders each message receives. */
public enum MessageKey {
    PLUGIN_ENABLED("plugin.enabled", "version"),
    COMMAND_USAGE("command.usage", "label"),
    COMMAND_NO_PERMISSION("command.no-permission"),
    COMMAND_UNKNOWN_SUBCOMMAND("command.unknown-subcommand", "subcommand", "label"),
    COMMAND_INVALID_SYNTAX("command.invalid-syntax", "usage"),
    RELOAD_SUCCESS("reload.success", "files", "duration"),
    RELOAD_FAILURE("reload.failure", "errors"),
    ERROR_INTERNAL("error.internal");

    private final String path;
    private final List<String> placeholders;

    MessageKey(String path, String... placeholders) {
        this.path = path;
        this.placeholders = List.of(placeholders);
    }

    /** YAML path in {@code messages.yml}. */
    public String path() {
        return path;
    }

    /** Placeholder names this message receives, for example {@code label} for {@code <label>}. */
    public List<String> placeholders() {
        return placeholders;
    }

    public static Optional<MessageKey> byPath(String path) {
        return Arrays.stream(values()).filter(key -> key.path.equals(path)).findFirst();
    }
}
