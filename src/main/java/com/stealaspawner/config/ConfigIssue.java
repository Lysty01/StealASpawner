package com.stealaspawner.config;

import java.util.Objects;

/**
 * A problem found while loading or validating a configuration file.
 *
 * @param severity {@link Severity#ERROR} blocks loading; {@link Severity#WARNING} does not
 * @param file     file name, for example {@code config.yml}
 * @param path     YAML path, for example {@code general.language}, or {@code null} for the whole file
 * @param reason   human-readable reason
 * @param expected what was expected, or {@code null}
 * @param actual   what was found, or {@code null}
 */
public record ConfigIssue(Severity severity, String file, String path, String reason, String expected, String actual) {

    private static final int MAX_VALUE_LENGTH = 80;

    public enum Severity { ERROR, WARNING }

    public ConfigIssue {
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(reason, "reason");
    }

    public static ConfigIssue error(ConfigFile file, String path, String reason, String expected, String actual) {
        return new ConfigIssue(Severity.ERROR, file.fileName(), path, reason, expected, actual);
    }

    public static ConfigIssue warning(ConfigFile file, String path, String reason, String expected, String actual) {
        return new ConfigIssue(Severity.WARNING, file.fileName(), path, reason, expected, actual);
    }

    /**
     * Multi-line diagnostic for the server console. Values are truncated and kept on one line.
     * Configuration files hold no secrets at this stage; values are shown so admins can fix them.
     */
    public String format() {
        StringBuilder out = new StringBuilder()
                .append(severity == Severity.ERROR ? "Configuration error:" : "Configuration warning:")
                .append("\nFile: ").append(file);
        if (path != null) {
            out.append("\nPath: ").append(path);
        }
        out.append("\nReason: ").append(reason);
        if (expected != null) {
            out.append("\nExpected: ").append(sanitize(expected));
        }
        if (actual != null) {
            out.append("\nActual: ").append(sanitize(actual));
        }
        return out.toString();
    }

    private static String sanitize(String value) {
        String oneLine = value.replace("\r", "\\r").replace("\n", "\\n");
        return oneLine.length() > MAX_VALUE_LENGTH ? oneLine.substring(0, MAX_VALUE_LENGTH) + "..." : oneLine;
    }
}
