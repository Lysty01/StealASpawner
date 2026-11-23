package com.stealaspawner.config;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable, fully validated configuration state.
 *
 * <p>A snapshot is only created when every managed file is valid. Components read the current
 * snapshot from {@link ConfigManager#snapshot()}; a reload replaces it atomically, so a reader
 * always sees one consistent state, never a mix of old and new values.</p>
 */
public record ConfigurationSnapshot(
        General general,
        Logging logging,
        Diagnostics diagnostics,
        Map<MessageKey, String> messages,
        Instant loadedAt) {

    /** {@code config.yml > general}. */
    public record General(String prefix, String language) {
        public General {
            Objects.requireNonNull(prefix, "prefix");
            Objects.requireNonNull(language, "language");
        }
    }

    /** {@code config.yml > logging}. */
    public record Logging(LogLevel level) {
        public Logging {
            Objects.requireNonNull(level, "level");
        }
    }

    /** {@code config.yml > diagnostics}. */
    public record Diagnostics(boolean startupSummary) {
    }

    public ConfigurationSnapshot {
        Objects.requireNonNull(general, "general");
        Objects.requireNonNull(logging, "logging");
        Objects.requireNonNull(diagnostics, "diagnostics");
        Objects.requireNonNull(loadedAt, "loadedAt");
        EnumMap<MessageKey, String> copy = new EnumMap<>(MessageKey.class);
        copy.putAll(Objects.requireNonNull(messages, "messages"));
        for (MessageKey key : MessageKey.values()) {
            if (!copy.containsKey(key)) {
                throw new IllegalArgumentException("Missing message " + key.path());
            }
        }
        messages = Map.copyOf(copy);
    }

    /** Raw MiniMessage template of a message. Never {@code null}. */
    public String message(MessageKey key) {
        return messages.get(key);
    }
}
