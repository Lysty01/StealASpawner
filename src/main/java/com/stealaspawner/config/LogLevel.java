package com.stealaspawner.config;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Console log levels accepted by {@code logging.level} in {@code config.yml}. */
public enum LogLevel {
    /** Only warnings and errors. */
    WARNING,
    /** Normal operation messages (default). */
    INFO,
    /** INFO plus detailed diagnostic messages prefixed with {@code [DEBUG]}. */
    DEBUG;

    /** Case-insensitive lookup. */
    public static Optional<LogLevel> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values()).filter(level -> level.name().equals(normalized)).findFirst();
    }

    /** Accepted values, for error messages and documentation. */
    public static String acceptedValues() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "));
    }
}
