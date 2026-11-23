package com.stealaspawner.config;

import com.stealaspawner.testutil.CapturingLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigManagerTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-23T12:00:00Z"), ZoneOffset.UTC);

    @TempDir
    Path dataFolder;

    private CapturingLog logs;

    @BeforeEach
    void setUp() {
        logs = new CapturingLog();
    }

    private ConfigManager newManager() {
        return new ConfigManager(dataFolder, ConfigManagerTest::bundled, logs.log(), CLOCK);
    }

    private static InputStream bundled(String name) {
        return ConfigManagerTest.class.getClassLoader().getResourceAsStream(name);
    }

    private static byte[] bundledBytes(String name) throws IOException {
        try (InputStream in = bundled(name)) {
            return in.readAllBytes();
        }
    }

    private Path file(ConfigFile file) {
        return dataFolder.resolve(file.fileName());
    }

    private void replaceInFile(ConfigFile file, String from, String to) throws IOException {
        String content = Files.readString(file(file), StandardCharsets.UTF_8);
        assertTrue(content.contains(from), () -> file.fileName() + " should contain " + from);
        Files.writeString(file(file), content.replace(from, to), StandardCharsets.UTF_8);
    }

    private static ConfigIssue onlyError(ConfigValidationException e) {
        assertEquals(1, e.errors().size(), () -> "errors: " + e.errors());
        return e.errors().get(0);
    }

    // 1. First start with missing files
    @Test
    void firstStartLoadsDefaultsWhenFilesAreMissing() throws Exception {
        ConfigurationSnapshot snapshot = newManager().load();

        assertEquals("en", snapshot.general().language());
        assertEquals(LogLevel.INFO, snapshot.logging().level());
        assertTrue(snapshot.diagnostics().startupSummary());
        assertTrue(snapshot.general().prefix().contains("StealASpawner"));
        assertEquals("StealASpawner <version> enabled.", snapshot.message(MessageKey.PLUGIN_ENABLED));
        assertEquals(CLOCK.instant(), snapshot.loadedAt());
        assertTrue(logs.messages(Level.WARNING).isEmpty(), () -> "unexpected warnings: " + logs.messages(Level.WARNING));
        assertTrue(logs.messages(Level.SEVERE).isEmpty());
    }

    // 2. Default files are created, identical to the bundled ones
    @Test
    void createsEveryDefaultFileIdenticalToTheBundledResource() throws Exception {
        newManager().load();

        for (ConfigFile file : ConfigFile.values()) {
            assertTrue(Files.isRegularFile(file(file)), file.fileName() + " should be created");
            assertArrayEquals(bundledBytes(file.fileName()), Files.readAllBytes(file(file)), file.fileName());
            assertTrue(logs.contains(Level.INFO, "Created default " + file.fileName()));
        }
    }

    @Test
    void createsOnlyMissingFilesWithoutOverwritingExistingOnes() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: DEBUG");
        byte[] customized = Files.readAllBytes(file(ConfigFile.CONFIG));
        Files.delete(file(ConfigFile.ECONOMY));
        logs.clear();

        ConfigurationSnapshot snapshot = newManager().load();

        assertArrayEquals(customized, Files.readAllBytes(file(ConfigFile.CONFIG)));
        assertTrue(Files.exists(file(ConfigFile.ECONOMY)));
        assertTrue(logs.contains(Level.INFO, "Created default economy.yml"));
        assertFalse(logs.contains(Level.INFO, "Created default config.yml"));
        assertEquals(LogLevel.DEBUG, snapshot.logging().level());
    }

    // 3. Custom values are loaded
    @Test
    void loadsCustomValues() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: debug");
        replaceInFile(ConfigFile.CONFIG, "startup-summary: true", "startup-summary: false");
        replaceInFile(ConfigFile.CONFIG,
                "prefix: \"<dark_gray>[<gradient:#c084fc:#7c3aed>StealASpawner</gradient>]</dark_gray> \"",
                "prefix: \"<gold>[SaS]</gold> \"");
        replaceInFile(ConfigFile.MESSAGES, "You don't have permission to use this command.", "Nope.");

        ConfigurationSnapshot snapshot = newManager().load();

        assertEquals(LogLevel.DEBUG, snapshot.logging().level());
        assertFalse(snapshot.diagnostics().startupSummary());
        assertEquals("<gold>[SaS]</gold> ", snapshot.general().prefix());
        assertEquals("<red>Nope.", snapshot.message(MessageKey.COMMAND_NO_PERMISSION));
    }

    @Test
    void emptyPrefixIsAllowed() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG,
                "prefix: \"<dark_gray>[<gradient:#c084fc:#7c3aed>StealASpawner</gradient>]</dark_gray> \"", "prefix: \"\"");

        assertEquals("", newManager().load().general().prefix());
    }

    // 4. Settings are kept after a simulated restart
    @Test
    void customSettingsSurviveARestartAndFilesAreNeverRewritten() throws Exception {
        ConfigManager first = newManager();
        first.load();
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: WARNING");
        Files.writeString(file(ConfigFile.SPAWNERS), "# my notes\nconfig-version: 1\nfuture-setting: 42\n", StandardCharsets.UTF_8);
        byte[] configBefore = Files.readAllBytes(file(ConfigFile.CONFIG));
        byte[] spawnersBefore = Files.readAllBytes(file(ConfigFile.SPAWNERS));
        first.shutdown();

        ConfigManager restarted = newManager();
        ConfigurationSnapshot snapshot = restarted.load();

        assertEquals(LogLevel.WARNING, snapshot.logging().level());
        assertArrayEquals(configBefore, Files.readAllBytes(file(ConfigFile.CONFIG)));
        assertArrayEquals(spawnersBefore, Files.readAllBytes(file(ConfigFile.SPAWNERS)));
    }

    @Test
    void unknownKeysProduceAWarningAndAreKept() throws Exception {
        newManager().load();
        Files.writeString(file(ConfigFile.ECONOMY), "config-version: 1\nstart-balance: 100\n", StandardCharsets.UTF_8);
        logs.clear();

        newManager().load();

        assertTrue(logs.contains(Level.WARNING, "Path: start-balance"));
        assertTrue(logs.contains(Level.WARNING, "unknown key, ignored"));
        assertTrue(Files.readString(file(ConfigFile.ECONOMY)).contains("start-balance: 100"));
    }

    @Test
    void missingOptionalValueUsesTheDefaultWithAWarning() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "  level: INFO\n", "");
        logs.clear();

        ConfigurationSnapshot snapshot = newManager().load();

        assertEquals(LogLevel.INFO, snapshot.logging().level());
        assertTrue(logs.contains(Level.WARNING, "Path: logging.level"));
    }

    // 5. Invalid YAML
    @Test
    void invalidYamlIsReportedWithItsLocationAndTheFileIsLeftUntouched() throws Exception {
        newManager().load();
        Files.writeString(file(ConfigFile.CONFIG), "config-version: 1\ngeneral:\n  language: en\n   prefix: [broken\n", StandardCharsets.UTF_8);
        byte[] broken = Files.readAllBytes(file(ConfigFile.CONFIG));

        ConfigValidationException e = assertThrows(ConfigValidationException.class, () -> newManager().load());

        ConfigIssue issue = onlyError(e);
        assertEquals("config.yml", issue.file());
        assertTrue(issue.reason().startsWith("invalid YAML syntax at line"), issue.reason());
        assertArrayEquals(broken, Files.readAllBytes(file(ConfigFile.CONFIG)));
        assertTrue(logs.contains(Level.SEVERE, "Configuration error:\nFile: config.yml\nReason: invalid YAML syntax"));
    }

    // 6. Wrong value type
    @Test
    void wrongValueTypeIsAnError() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "startup-summary: true", "startup-summary: maybe");

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("diagnostics.startup-summary", issue.path());
        assertEquals("wrong value type", issue.reason());
        assertEquals("true or false", issue.expected());
        assertEquals("\"maybe\" (text)", issue.actual());
    }

    @Test
    void unsupportedLanguageProducesTheDocumentedDiagnostic() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "language: en", "language: xx");

        ConfigValidationException e = assertThrows(ConfigValidationException.class, () -> newManager().load());

        assertEquals("""
                Configuration error:
                File: config.yml
                Path: general.language
                Reason: unsupported language
                Expected: en
                Actual: xx""", onlyError(e).format());
    }

    @Test
    void unsupportedLogLevelIsAnError() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: VERBOSE");

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("logging.level", issue.path());
        assertEquals("WARNING, INFO, DEBUG", issue.expected());
    }

    @Test
    void unsupportedConfigVersionIsAnError() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.COLLECTORS, "config-version: 1", "config-version: 2");

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("collectors.yml", issue.file());
        assertEquals("config-version", issue.path());
    }

    @Test
    void sectionReplacedByAValueIsReportedOnce() throws Exception {
        newManager().load();
        Files.writeString(file(ConfigFile.CONFIG), "config-version: 1\ngeneral: oops\n", StandardCharsets.UTF_8);

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("general", issue.path());
        assertEquals("section", issue.expected());
    }

    // 7. Missing required value
    @Test
    void missingRequiredValueIsAnError() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "  language: en\n", "");

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("general.language", issue.path());
        assertEquals("required value is missing", issue.reason());
    }

    @Test
    void emptyRequiredValueIsAnError() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.MESSAGES, "no-permission: \"<red>You don't have permission to use this command.\"", "no-permission: \"  \"");

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("command.no-permission", issue.path());
        assertEquals("message is empty", issue.reason());
    }

    @Test
    void messagesLanguageMustMatchConfigLanguage() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.MESSAGES, "language: en", "language: fr");

        ConfigIssue issue = onlyError(assertThrows(ConfigValidationException.class, () -> newManager().load()));

        assertEquals("messages.yml", issue.file());
        assertEquals("language", issue.path());
    }

    @Test
    void everyErrorIsReportedAtOnce() throws Exception {
        newManager().load();
        replaceInFile(ConfigFile.CONFIG, "language: en", "language: xx");
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: 3");

        ConfigValidationException e = assertThrows(ConfigValidationException.class, () -> newManager().load());

        assertEquals(List.of("general.language", "logging.level"), e.errors().stream().map(ConfigIssue::path).toList());
    }

    // 8. Successful reload
    @Test
    void successfulReloadPublishesANewSnapshot() throws Exception {
        ConfigManager manager = newManager();
        ConfigurationSnapshot before = manager.load();
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: DEBUG");

        ReloadResult result = manager.reload();

        assertTrue(result.success());
        assertTrue(result.errors().isEmpty());
        assertEquals(ConfigFile.values().length, result.fileCount());
        assertNotSame(before, manager.snapshot());
        assertEquals(LogLevel.DEBUG, manager.snapshot().logging().level());
        assertEquals(LogLevel.DEBUG, logs.log().level());
    }

    // 9. Failed reload keeps the previous snapshot
    @Test
    void failedReloadKeepsThePreviousSnapshot() throws Exception {
        ConfigManager manager = newManager();
        manager.load();
        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: WARNING");
        assertTrue(manager.reload().success());
        ConfigurationSnapshot active = manager.snapshot();

        replaceInFile(ConfigFile.CONFIG, "language: en", "language: xx");
        replaceInFile(ConfigFile.CONFIG, "level: WARNING", "level: DEBUG");
        ReloadResult result = manager.reload();

        assertFalse(result.success());
        assertEquals(1, result.errors().size());
        assertSame(active, manager.snapshot());
        assertEquals(LogLevel.WARNING, manager.snapshot().logging().level());
        assertEquals(LogLevel.WARNING, logs.log().level(), "the log level must not change on a failed reload");
        assertTrue(logs.contains(Level.WARNING, "Reload failed with 1 error(s). The previous configuration is still active."));
    }

    @Test
    void failedReloadWithBrokenYamlKeepsThePreviousSnapshot() throws Exception {
        ConfigManager manager = newManager();
        ConfigurationSnapshot active = manager.load();
        Files.writeString(file(ConfigFile.MESSAGES), "language: [en\n", StandardCharsets.UTF_8);

        ReloadResult result = manager.reload();

        assertFalse(result.success());
        assertSame(active, manager.snapshot());
    }

    @Test
    void reloadRecreatesADeletedFile() throws Exception {
        ConfigManager manager = newManager();
        manager.load();
        Files.delete(file(ConfigFile.SPAWNERS));

        assertTrue(manager.reload().success());
        assertTrue(Files.exists(file(ConfigFile.SPAWNERS)));
    }

    @Test
    void snapshotIsUnavailableBeforeLoadAndAfterShutdown() throws Exception {
        ConfigManager manager = newManager();
        assertThrows(IllegalStateException.class, manager::snapshot);
        manager.load();
        assertTrue(manager.isLoaded());
        manager.shutdown();
        assertFalse(manager.isLoaded());
        assertThrows(IllegalStateException.class, manager::snapshot);
    }

    @Test
    void startupSummaryIsLoggedOnlyWhenEnabled() throws Exception {
        newManager().load();
        assertTrue(logs.contains(Level.INFO, "Configuration loaded: 5 files, language=en, log level=INFO"));

        replaceInFile(ConfigFile.CONFIG, "startup-summary: true", "startup-summary: false");
        logs.clear();
        newManager().load();
        assertFalse(logs.contains(Level.INFO, "Configuration loaded"));
    }

    @Test
    void debugLevelLogsDiagnosticMessages() throws Exception {
        ConfigManager manager = newManager();
        manager.load();
        assertFalse(logs.contains(Level.INFO, "[DEBUG]"));

        replaceInFile(ConfigFile.CONFIG, "level: INFO", "level: DEBUG");
        manager.reload();
        logs.clear();
        manager.reload();

        assertTrue(logs.contains(Level.INFO, "[DEBUG] Read config.yml"));
    }
}
