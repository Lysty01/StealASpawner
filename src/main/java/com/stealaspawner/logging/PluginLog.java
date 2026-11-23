package com.stealaspawner.logging;

import com.stealaspawner.config.LogLevel;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin wrapper around the plugin logger that applies the configured {@link LogLevel}.
 *
 * <p>Paper's console only shows messages at INFO or above, so debug messages are written at
 * INFO with a {@code [DEBUG]} prefix, and only when the configured level is {@link LogLevel#DEBUG}.</p>
 */
public final class PluginLog {

    private final Logger logger;
    private volatile LogLevel level = LogLevel.INFO;

    public PluginLog(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    /** Applies a new log level. {@link LogLevel#WARNING} hides the plugin's INFO messages. */
    public void applyLevel(LogLevel newLevel) {
        this.level = Objects.requireNonNull(newLevel, "newLevel");
        logger.setLevel(newLevel == LogLevel.WARNING ? Level.WARNING : null);
    }

    public LogLevel level() {
        return level;
    }

    public void debug(Supplier<String> message) {
        if (level == LogLevel.DEBUG) {
            logger.info("[DEBUG] " + message.get());
        }
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }

    public void severe(String message) {
        logger.severe(message);
    }

    public void severe(String message, Throwable error) {
        logger.log(Level.SEVERE, message, error);
    }
}
