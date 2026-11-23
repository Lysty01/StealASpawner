package com.stealaspawner.config;

import com.stealaspawner.logging.PluginLog;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Loads, validates and publishes the plugin configuration.
 *
 * <ul>
 *   <li>Missing files are created from the defaults bundled in the plugin jar. Existing files are
 *       never overwritten, rewritten or deleted: the plugin only reads them.</li>
 *   <li>Loading is transactional: all files are read and validated into temporary objects, and the
 *       new {@link ConfigurationSnapshot} is published only if there is no error. Otherwise the
 *       previous snapshot stays active.</li>
 *   <li>Reads go through {@link #snapshot()}, which always returns a complete, validated state.</li>
 * </ul>
 *
 * <p>Files are small and loading only happens at startup and on an explicit reload, so it runs on
 * the calling thread.</p>
 */
public final class ConfigManager {

    /** Opens a bundled resource by name, or returns {@code null} when it does not exist. */
    @FunctionalInterface
    public interface ResourceProvider {
        InputStream open(String name) throws IOException;
    }

    private final Path dataFolder;
    private final ResourceProvider resources;
    private final PluginLog log;
    private final Clock clock;
    private final ConfigValidator validator = new ConfigValidator();
    private final Object loadLock = new Object();

    private volatile ConfigurationSnapshot snapshot;
    private Map<ConfigFile, YamlConfiguration> defaults;

    public ConfigManager(Path dataFolder, ResourceProvider resources, PluginLog log) {
        this(dataFolder, resources, log, Clock.systemUTC());
    }

    public ConfigManager(Path dataFolder, ResourceProvider resources, PluginLog log, Clock clock) {
        this.dataFolder = Objects.requireNonNull(dataFolder, "dataFolder");
        this.resources = Objects.requireNonNull(resources, "resources");
        this.log = Objects.requireNonNull(log, "log");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Initial load at startup.
     *
     * @throws ConfigValidationException if any file is unreadable or invalid; errors are already logged
     */
    public ConfigurationSnapshot load() throws ConfigValidationException {
        synchronized (loadLock) {
            Attempt attempt = attempt();
            if (attempt.snapshot == null) {
                throw new ConfigValidationException(attempt.errors);
            }
            publish(attempt.snapshot, attempt.durationMillis());
            return attempt.snapshot;
        }
    }

    /** Reloads every file. On failure the active snapshot is kept unchanged. */
    public ReloadResult reload() {
        synchronized (loadLock) {
            Attempt attempt = attempt();
            if (attempt.snapshot == null) {
                log.warning("Reload failed with " + attempt.errors.size()
                        + " error(s). The previous configuration is still active.");
                return new ReloadResult(false, attempt.errors, attempt.warnings, ConfigFile.values().length, attempt.duration);
            }
            publish(attempt.snapshot, attempt.durationMillis());
            return new ReloadResult(true, List.of(), attempt.warnings, ConfigFile.values().length, attempt.duration);
        }
    }

    /**
     * Current validated configuration.
     *
     * @throws IllegalStateException if the configuration is not loaded (before startup or after shutdown)
     */
    public ConfigurationSnapshot snapshot() {
        ConfigurationSnapshot current = snapshot;
        if (current == null) {
            throw new IllegalStateException("Configuration is not loaded");
        }
        return current;
    }

    public boolean isLoaded() {
        return snapshot != null;
    }

    public Path dataFolder() {
        return dataFolder;
    }

    /** Releases the active configuration when the plugin is disabled. Files are not touched. */
    public void shutdown() {
        synchronized (loadLock) {
            snapshot = null;
        }
    }

    private void publish(ConfigurationSnapshot newSnapshot, long durationMillis) {
        snapshot = newSnapshot;
        log.applyLevel(newSnapshot.logging().level());
        if (newSnapshot.diagnostics().startupSummary()) {
            log.info("Configuration loaded: " + ConfigFile.values().length + " files, language="
                    + newSnapshot.general().language() + ", log level=" + newSnapshot.logging().level()
                    + " (" + durationMillis + " ms).");
        }
    }

    private Attempt attempt() {
        long start = System.nanoTime();
        List<ConfigIssue> errors = new ArrayList<>();
        Map<ConfigFile, YamlConfiguration> parsed = new EnumMap<>(ConfigFile.class);

        Map<ConfigFile, YamlConfiguration> bundled = defaults();
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            errors.add(new ConfigIssue(ConfigIssue.Severity.ERROR, dataFolder.getFileName().toString(), null,
                    "cannot create the plugin folder (" + e.getClass().getSimpleName() + ")", "writable folder", null));
            return finish(start, null, errors, List.of());
        }

        for (ConfigFile file : ConfigFile.values()) {
            Path path = dataFolder.resolve(file.fileName());
            if (Files.notExists(path)) {
                try {
                    createDefault(file, path);
                } catch (IOException e) {
                    errors.add(ConfigIssue.error(file, null,
                            "file is missing and cannot be created (" + e.getClass().getSimpleName() + ")", "writable file", null));
                    continue;
                }
            }
            readFile(file, path, errors).ifPresent(yaml -> parsed.put(file, yaml));
        }
        if (!errors.isEmpty()) {
            errors.forEach(issue -> log.severe(issue.format()));
            return finish(start, null, errors, List.of());
        }

        ConfigValidator.Result result = validator.validate(parsed, bundled, clock.instant());
        result.warnings().forEach(issue -> log.warning(issue.format()));
        result.errors().forEach(issue -> log.severe(issue.format()));
        return finish(start, result.snapshot(), result.errors(), result.warnings());
    }

    private Optional<YamlConfiguration> readFile(ConfigFile file, Path path, List<ConfigIssue> errors) {
        if (!Files.isRegularFile(path)) {
            errors.add(ConfigIssue.error(file, null, "not a regular file", "YAML file", null));
            return Optional.empty();
        }
        String content;
        try {
            content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (CharacterCodingException e) {
            errors.add(ConfigIssue.error(file, null, "file is not valid UTF-8 text", "UTF-8 encoded file", null));
            return Optional.empty();
        } catch (IOException e) {
            errors.add(ConfigIssue.error(file, null, "file cannot be read (" + e.getClass().getSimpleName() + ")", "readable file", null));
            return Optional.empty();
        }
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.loadFromString(content);
        } catch (InvalidConfigurationException e) {
            errors.add(yamlSyntaxError(file, e));
            return Optional.empty();
        }
        log.debug(() -> "Read " + file.fileName() + " (" + yaml.getKeys(true).size() + " keys)");
        return Optional.of(yaml);
    }

    private void createDefault(ConfigFile file, Path path) throws IOException {
        try (InputStream in = resources.open(file.fileName())) {
            if (in == null) {
                throw new IllegalStateException("Bundled default " + file.fileName() + " is missing from the plugin jar");
            }
            Files.copy(in, path);
        }
        log.info("Created default " + file.fileName());
    }

    private Map<ConfigFile, YamlConfiguration> defaults() {
        if (defaults != null) {
            return defaults;
        }
        Map<ConfigFile, YamlConfiguration> loaded = new EnumMap<>(ConfigFile.class);
        for (ConfigFile file : ConfigFile.values()) {
            try (InputStream in = resources.open(file.fileName())) {
                if (in == null) {
                    throw new IllegalStateException("Bundled default " + file.fileName() + " is missing from the plugin jar");
                }
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.loadFromString(new String(in.readAllBytes(), StandardCharsets.UTF_8));
                loaded.put(file, yaml);
            } catch (IOException | InvalidConfigurationException e) {
                throw new IllegalStateException("Bundled default " + file.fileName() + " cannot be read", e);
            }
        }
        defaults = Map.copyOf(loaded);
        return defaults;
    }

    private static ConfigIssue yamlSyntaxError(ConfigFile file, InvalidConfigurationException e) {
        Throwable cause = e.getCause();
        if (cause instanceof MarkedYAMLException marked) {
            Mark mark = marked.getProblemMark() != null ? marked.getProblemMark() : marked.getContextMark();
            String location = mark == null ? "" : " at line " + (mark.getLine() + 1) + ", column " + (mark.getColumn() + 1);
            String problem = marked.getProblem() != null ? marked.getProblem() : marked.getContext();
            return ConfigIssue.error(file, null, "invalid YAML syntax" + location, "valid YAML", problem);
        }
        String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage().lines().findFirst().orElse("");
        return ConfigIssue.error(file, null, "invalid YAML syntax", "valid YAML", message);
    }

    private static Attempt finish(long start, ConfigurationSnapshot snapshot, List<ConfigIssue> errors, List<ConfigIssue> warnings) {
        return new Attempt(snapshot, List.copyOf(errors), List.copyOf(warnings), Duration.ofNanos(System.nanoTime() - start));
    }

    private record Attempt(ConfigurationSnapshot snapshot, List<ConfigIssue> errors, List<ConfigIssue> warnings, Duration duration) {
        long durationMillis() {
            return duration.toMillis();
        }
    }
}
