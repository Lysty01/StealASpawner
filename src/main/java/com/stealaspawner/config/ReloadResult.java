package com.stealaspawner.config;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Outcome of {@link ConfigManager#reload()}.
 *
 * @param success   {@code true} when the new configuration was published
 * @param errors    errors that prevented the reload (empty on success)
 * @param warnings  non-blocking issues
 * @param fileCount number of managed files
 * @param duration  time spent loading and validating
 */
public record ReloadResult(boolean success, List<ConfigIssue> errors, List<ConfigIssue> warnings, int fileCount, Duration duration) {

    public ReloadResult {
        errors = List.copyOf(errors);
        warnings = List.copyOf(warnings);
        Objects.requireNonNull(duration, "duration");
    }
}
