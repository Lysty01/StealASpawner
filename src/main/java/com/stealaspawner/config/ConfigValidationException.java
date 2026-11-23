package com.stealaspawner.config;

import java.util.List;

/** Thrown when the configuration cannot be loaded because of one or more {@link ConfigIssue.Severity#ERROR errors}. */
public final class ConfigValidationException extends Exception {

    private final transient List<ConfigIssue> errors;

    public ConfigValidationException(List<ConfigIssue> errors) {
        super(errors.size() + " configuration error(s)");
        this.errors = List.copyOf(errors);
    }

    public List<ConfigIssue> errors() {
        return errors;
    }
}
