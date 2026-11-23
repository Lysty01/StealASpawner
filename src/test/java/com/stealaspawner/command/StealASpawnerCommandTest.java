package com.stealaspawner.command;

import com.stealaspawner.StealASpawnerPlugin;
import com.stealaspawner.config.ConfigurationSnapshot;
import com.stealaspawner.config.LogLevel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.command.MessageTarget;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StealASpawnerCommandTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private static final String PREFIX = "[StealASpawner] ";

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

    private static String next(MessageTarget target) {
        Component message = target.nextComponentMessage();
        assertNotNull(message, "a message was expected");
        return PLAIN.serialize(message);
    }

    private void setLogLevel(String from, String to) throws IOException {
        Path config = plugin.getDataFolder().toPath().resolve("config.yml");
        String content = Files.readString(config, StandardCharsets.UTF_8);
        Files.writeString(config, content.replace("level: " + from, "level: " + to), StandardCharsets.UTF_8);
    }

    private void breakLanguage() throws IOException {
        Path config = plugin.getDataFolder().toPath().resolve("config.yml");
        String content = Files.readString(config, StandardCharsets.UTF_8);
        Files.writeString(config, content.replace("language: en", "language: xx"), StandardCharsets.UTF_8);
    }

    // 13. Reload permission denied
    @Test
    void playerWithoutPermissionCannotReload() throws IOException {
        PlayerMock player = server.addPlayer();
        ConfigurationSnapshot before = plugin.configManager().snapshot();
        setLogLevel("INFO", "DEBUG");

        player.performCommand("sas reload");

        assertEquals(PREFIX + "You don't have permission to use this command.", next(player));
        assertSame(before, plugin.configManager().snapshot(), "nothing must be reloaded");
    }

    @Test
    void playerWithoutPermissionGetsNoPermissionForAnySubcommand() {
        PlayerMock player = server.addPlayer();

        player.performCommand("sas");
        player.performCommand("sas whatever");

        assertEquals(PREFIX + "You don't have permission to use this command.", next(player));
        assertEquals(PREFIX + "You don't have permission to use this command.", next(player));
    }

    // 14. Reload permission granted
    @Test
    void playerWithPermissionReloads() throws IOException {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, StealASpawnerCommand.RELOAD_PERMISSION, true);
        ConfigurationSnapshot before = plugin.configManager().snapshot();
        setLogLevel("INFO", "DEBUG");

        player.performCommand("sas reload");

        String message = next(player);
        assertTrue(message.startsWith(PREFIX + "Configuration reloaded (5 files, "), message);
        assertNotSame(before, plugin.configManager().snapshot());
        assertEquals(LogLevel.DEBUG, plugin.configManager().snapshot().logging().level());
    }

    @Test
    void operatorHasThePermissionByDefault() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        player.performCommand("stealaspawner reload");

        assertTrue(next(player).startsWith(PREFIX + "Configuration reloaded"));
    }

    @Test
    void failedReloadReportsTheErrorCountAndKeepsTheOldSnapshot() throws IOException {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        ConfigurationSnapshot before = plugin.configManager().snapshot();
        breakLanguage();

        player.performCommand("sas reload");

        assertEquals(PREFIX + "Reload failed: 1 error(s). The previous configuration is still active. "
                + "See the server console for details.", next(player));
        assertSame(before, plugin.configManager().snapshot());
    }

    // 15. Reload from the console
    @Test
    void consoleCanReload() {
        ConsoleCommandSenderMock console = server.getConsoleSender();

        server.dispatchCommand(console, "sas reload");

        assertTrue(next(console).startsWith(PREFIX + "Configuration reloaded (5 files, "));
    }

    @Test
    void usageUnknownSubcommandAndInvalidSyntaxMessages() {
        ConsoleCommandSenderMock console = server.getConsoleSender();

        server.dispatchCommand(console, "sas");
        server.dispatchCommand(console, "sas fly");
        server.dispatchCommand(console, "sas reload now");

        assertEquals(PREFIX + "Usage: /sas reload", next(console));
        assertEquals(PREFIX + "Unknown subcommand: fly. Usage: /sas reload", next(console));
        assertEquals(PREFIX + "Invalid syntax. Usage: /sas reload", next(console));
    }

    // 16. Tab completion depends on the permission
    @Test
    void tabCompletionOnlyOffersReloadToAllowedSenders() {
        PluginCommand command = plugin.getCommand("stealaspawner");
        assertNotNull(command);
        PlayerMock allowed = server.addPlayer();
        allowed.addAttachment(plugin, StealASpawnerCommand.RELOAD_PERMISSION, true);
        PlayerMock denied = server.addPlayer();

        assertEquals(List.of("reload"), command.tabComplete(allowed, "sas", new String[]{""}));
        assertEquals(List.of("reload"), command.tabComplete(allowed, "sas", new String[]{"RE"}));
        assertEquals(List.of(), command.tabComplete(allowed, "sas", new String[]{"x"}));
        assertEquals(List.of(), command.tabComplete(allowed, "sas", new String[]{"reload", ""}));
        assertEquals(List.of(), command.tabComplete(denied, "sas", new String[]{""}));
        assertEquals(List.of("reload"), command.tabComplete(server.getConsoleSender(), "sas", new String[]{"r"}));
    }

    @Test
    void aliasesAndPermissionAreDeclared() {
        PluginCommand command = plugin.getCommand("stealaspawner");
        assertNotNull(command);
        assertEquals(List.of("sas"), command.getAliases());
        assertNotNull(server.getPluginManager().getPermission(StealASpawnerCommand.RELOAD_PERMISSION));
        assertEquals(org.bukkit.permissions.PermissionDefault.OP,
                server.getPluginManager().getPermission(StealASpawnerCommand.RELOAD_PERMISSION).getDefault());
    }
}
