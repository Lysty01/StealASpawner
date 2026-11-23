package com.stealaspawner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StealASpawnerPluginTest {

    private ServerMock server;
    private StealASpawnerPlugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(StealASpawnerPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void pluginEnablesWithoutError() {
        assertTrue(plugin.isEnabled());
    }

    @Test
    void pluginMetadataIsCorrect() {
        assertEquals("StealASpawner", plugin.getPluginMeta().getName());
        assertEquals("0.1.0-SNAPSHOT", plugin.getPluginMeta().getVersion());
    }

    @Test
    void pluginDisablesCleanly() {
        server.getPluginManager().disablePlugin(plugin);
        assertFalse(plugin.isEnabled());
    }
}
