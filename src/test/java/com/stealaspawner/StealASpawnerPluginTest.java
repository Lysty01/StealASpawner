package com.stealaspawner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StealASpawnerPluginTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void pluginEnablesAndCreatesTheDefaultFiles() {
        StealASpawnerPlugin plugin = MockBukkit.load(StealASpawnerPlugin.class);

        assertTrue(plugin.isEnabled());
        assertTrue(plugin.configManager().isLoaded());
        for (String file : new String[]{"config.yml", "messages.yml", "spawners.yml", "economy.yml", "collectors.yml"}) {
            assertTrue(new File(plugin.getDataFolder(), file).isFile(), file);
        }
    }

    @Test
    void pluginMetadataIsCorrect() {
        StealASpawnerPlugin plugin = MockBukkit.load(StealASpawnerPlugin.class);

        assertEquals("StealASpawner", plugin.getPluginMeta().getName());
        assertEquals("0.1.0-SNAPSHOT", plugin.getPluginMeta().getVersion());
    }

    @Test
    void pluginDisablesCleanly() {
        StealASpawnerPlugin plugin = MockBukkit.load(StealASpawnerPlugin.class);

        server.getPluginManager().disablePlugin(plugin);

        assertFalse(plugin.isEnabled());
        assertFalse(plugin.configManager().isLoaded());
    }

    @Test
    void invalidConfigurationAtStartupDisablesThePluginWithoutTouchingTheFile() throws Exception {
        // MockBukkit names the plugin data folder "<name>-<version>".
        File dataFolder = server.getPluginManager().createTemporaryDirectory("StealASpawner-0.1.0-SNAPSHOT");
        Path config = dataFolder.toPath().resolve("config.yml");
        byte[] invalid = "config-version: 1\ngeneral:\n  language: xx\n".getBytes(StandardCharsets.UTF_8);
        Files.write(config, invalid);

        StealASpawnerPlugin plugin = MockBukkit.load(StealASpawnerPlugin.class);

        assertEquals(dataFolder.getCanonicalFile(), plugin.getDataFolder().getCanonicalFile());
        assertFalse(plugin.isEnabled(), "the plugin must not run with an invalid configuration");
        assertArrayEquals(invalid, Files.readAllBytes(config), "the invalid file must not be modified");
    }
}
