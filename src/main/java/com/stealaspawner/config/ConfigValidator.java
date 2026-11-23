package com.stealaspawner.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Validates parsed configuration files and builds a {@link ConfigurationSnapshot}.
 *
 * <p>This class performs no I/O: it receives already parsed files and the bundled defaults.</p>
 *
 * <h2>Policy</h2>
 * <ul>
 *   <li><b>Invalid values</b> (wrong type, unsupported value, empty required text) are
 *       {@linkplain ConfigIssue.Severity#ERROR errors}. They are never replaced by defaults.</li>
 *   <li><b>Missing required values</b> ({@code config-version}, {@code general.language},
 *       {@code language} in {@code messages.yml}) are errors.</li>
 *   <li><b>Missing optional values</b> use the bundled default and produce a
 *       {@linkplain ConfigIssue.Severity#WARNING warning}.</li>
 *   <li><b>Unknown keys</b> produce a warning, are ignored and are left untouched in the file.</li>
 * </ul>
 */
public final class ConfigValidator {

    /** Languages for which a {@code messages.yml} is provided. */
    public static final Set<String> SUPPORTED_LANGUAGES = Set.of("en");

    static final String VERSION_PATH = "config-version";

    /** Result of a validation: a snapshot when there is no error, otherwise {@code null}. */
    public record Result(ConfigurationSnapshot snapshot, List<ConfigIssue> errors, List<ConfigIssue> warnings) {
        public Result {
            errors = List.copyOf(errors);
            warnings = List.copyOf(warnings);
        }

        public boolean valid() {
            return errors.isEmpty();
        }
    }

    public Result validate(Map<ConfigFile, YamlConfiguration> files, Map<ConfigFile, YamlConfiguration> defaults, Instant now) {
        Objects.requireNonNull(files, "files");
        Objects.requireNonNull(defaults, "defaults");
        Context ctx = new Context();

        for (ConfigFile file : ConfigFile.values()) {
            YamlConfiguration yaml = Objects.requireNonNull(files.get(file), () -> "missing parsed file " + file);
            YamlConfiguration defaultYaml = Objects.requireNonNull(defaults.get(file), () -> "missing default " + file);
            checkVersion(ctx, file, yaml);
            checkUnknownKeys(ctx, file, yaml, defaultYaml);
        }

        // config.yml
        YamlConfiguration config = files.get(ConfigFile.CONFIG);
        YamlConfiguration configDefaults = defaults.get(ConfigFile.CONFIG);
        for (String section : List.of("general", "logging", "diagnostics")) {
            checkSection(ctx, ConfigFile.CONFIG, config, section);
        }
        String prefix = optionalString(ctx, ConfigFile.CONFIG, config, configDefaults, "general.prefix");
        String language = requiredString(ctx, ConfigFile.CONFIG, config, "general.language");
        if (language != null && !SUPPORTED_LANGUAGES.contains(language)) {
            ctx.error(ConfigFile.CONFIG, "general.language", "unsupported language",
                    String.join(", ", SUPPORTED_LANGUAGES), language);
            language = null;
        }
        LogLevel level = optionalLogLevel(ctx, config, configDefaults);
        Boolean startupSummary = optionalBoolean(ctx, ConfigFile.CONFIG, config, configDefaults, "diagnostics.startup-summary");

        // messages.yml
        YamlConfiguration messagesYaml = files.get(ConfigFile.MESSAGES);
        YamlConfiguration messageDefaults = defaults.get(ConfigFile.MESSAGES);
        String messagesLanguage = requiredString(ctx, ConfigFile.MESSAGES, messagesYaml, "language");
        if (language != null && messagesLanguage != null && !language.equals(messagesLanguage)) {
            ctx.error(ConfigFile.MESSAGES, "language",
                    "messages.yml language does not match general.language in config.yml", language, messagesLanguage);
        }
        Map<MessageKey, String> messages = new EnumMap<>(MessageKey.class);
        for (MessageKey key : MessageKey.values()) {
            String template = message(ctx, messagesYaml, messageDefaults, key);
            if (template != null) {
                messages.put(key, template);
            }
        }

        if (!ctx.errors.isEmpty()) {
            return new Result(null, ctx.errors, ctx.warnings);
        }
        ConfigurationSnapshot snapshot = new ConfigurationSnapshot(
                new ConfigurationSnapshot.General(prefix, language),
                new ConfigurationSnapshot.Logging(level),
                new ConfigurationSnapshot.Diagnostics(startupSummary),
                messages,
                now);
        return new Result(snapshot, ctx.errors, ctx.warnings);
    }

    private static void checkVersion(Context ctx, ConfigFile file, YamlConfiguration yaml) {
        Object value = yaml.get(VERSION_PATH);
        String expected = String.valueOf(ConfigFile.SUPPORTED_VERSION);
        if (value == null) {
            ctx.error(file, VERSION_PATH, "required value is missing", expected, null);
        } else if (!(value instanceof Integer version)) {
            ctx.error(file, VERSION_PATH, "wrong value type", "whole number " + expected, describe(value));
        } else if (version != ConfigFile.SUPPORTED_VERSION) {
            ctx.error(file, VERSION_PATH, "unsupported configuration version", expected, String.valueOf(version));
        }
    }

    private static void checkUnknownKeys(Context ctx, ConfigFile file, YamlConfiguration yaml, YamlConfiguration defaults) {
        Set<String> known = defaults.getKeys(true);
        Set<String> unknown = new HashSet<>();
        for (String key : yaml.getKeys(true)) {
            if (known.contains(key)) {
                continue;
            }
            unknown.add(key);
            int dot = key.lastIndexOf('.');
            String parent = dot < 0 ? null : key.substring(0, dot);
            boolean reportedAbove = parent != null && (unknown.contains(parent) || !defaults.isConfigurationSection(parent));
            if (!reportedAbove) {
                ctx.warning(file, key, "unknown key, ignored", null, null);
            }
        }
    }

    private static void checkSection(Context ctx, ConfigFile file, YamlConfiguration yaml, String path) {
        Object value = yaml.get(path);
        if (value != null && !(value instanceof ConfigurationSection)) {
            ctx.error(file, path, "wrong value type", "section", describe(value));
            ctx.invalidSections.add(path);
        }
    }

    private static String requiredString(Context ctx, ConfigFile file, YamlConfiguration yaml, String path) {
        if (ctx.underInvalidSection(path)) {
            return null;
        }
        Object value = yaml.get(path);
        if (value == null) {
            ctx.error(file, path, "required value is missing", "text", null);
            return null;
        }
        if (!(value instanceof String text)) {
            ctx.error(file, path, "wrong value type", "text", describe(value));
            return null;
        }
        if (text.isBlank()) {
            ctx.error(file, path, "required value is empty", "non-empty text", "\"\"");
            return null;
        }
        return text.trim();
    }

    private static String optionalString(Context ctx, ConfigFile file, YamlConfiguration yaml, YamlConfiguration defaults, String path) {
        if (ctx.underInvalidSection(path)) {
            return null;
        }
        Object value = yaml.get(path);
        if (value == null) {
            String fallback = defaults.getString(path);
            ctx.warning(file, path, "value is missing, using the default value", null, null);
            return fallback;
        }
        if (!(value instanceof String text)) {
            ctx.error(file, path, "wrong value type", "text", describe(value));
            return null;
        }
        return text;
    }

    private static Boolean optionalBoolean(Context ctx, ConfigFile file, YamlConfiguration yaml, YamlConfiguration defaults, String path) {
        if (ctx.underInvalidSection(path)) {
            return null;
        }
        Object value = yaml.get(path);
        if (value == null) {
            ctx.warning(file, path, "value is missing, using the default value", null, null);
            return defaults.getBoolean(path);
        }
        if (!(value instanceof Boolean bool)) {
            ctx.error(file, path, "wrong value type", "true or false", describe(value));
            return null;
        }
        return bool;
    }

    private static LogLevel optionalLogLevel(Context ctx, YamlConfiguration yaml, YamlConfiguration defaults) {
        String path = "logging.level";
        if (ctx.underInvalidSection(path)) {
            return null;
        }
        Object value = yaml.get(path);
        if (value == null) {
            ctx.warning(ConfigFile.CONFIG, path, "value is missing, using the default value", null, null);
            return LogLevel.parse(defaults.getString(path)).orElse(LogLevel.INFO);
        }
        if (!(value instanceof String text)) {
            ctx.error(ConfigFile.CONFIG, path, "wrong value type", LogLevel.acceptedValues(), describe(value));
            return null;
        }
        return LogLevel.parse(text).orElseGet(() -> {
            ctx.error(ConfigFile.CONFIG, path, "unsupported log level", LogLevel.acceptedValues(), text);
            return null;
        });
    }

    private static String message(Context ctx, YamlConfiguration yaml, YamlConfiguration defaults, MessageKey key) {
        Object value = yaml.get(key.path());
        if (value == null) {
            ctx.warning(ConfigFile.MESSAGES, key.path(), "message is missing, using the built-in default", null, null);
            return defaults.getString(key.path());
        }
        if (!(value instanceof String text)) {
            ctx.error(ConfigFile.MESSAGES, key.path(), "wrong value type", "text", describe(value));
            return null;
        }
        if (text.isBlank()) {
            ctx.error(ConfigFile.MESSAGES, key.path(), "message is empty", "non-empty text", "\"\"");
            return null;
        }
        return text;
    }

    /** Human-readable description of a YAML value, for diagnostics. */
    static String describe(Object value) {
        if (value instanceof ConfigurationSection) {
            return "(section)";
        }
        if (value instanceof String text) {
            return "\"" + text + "\" (text)";
        }
        if (value instanceof Boolean) {
            return value + " (true/false)";
        }
        if (value instanceof Number) {
            return value + " (number)";
        }
        if (value instanceof List<?>) {
            return value + " (list)";
        }
        return value + " (" + value.getClass().getSimpleName() + ")";
    }

    private static final class Context {
        private final List<ConfigIssue> errors = new ArrayList<>();
        private final List<ConfigIssue> warnings = new ArrayList<>();
        private final Set<String> invalidSections = new HashSet<>();

        void error(ConfigFile file, String path, String reason, String expected, String actual) {
            errors.add(ConfigIssue.error(file, path, reason, expected, actual));
        }

        void warning(ConfigFile file, String path, String reason, String expected, String actual) {
            warnings.add(ConfigIssue.warning(file, path, reason, expected, actual));
        }

        boolean underInvalidSection(String path) {
            return invalidSections.stream().anyMatch(section -> path.startsWith(section + "."));
        }
    }
}
