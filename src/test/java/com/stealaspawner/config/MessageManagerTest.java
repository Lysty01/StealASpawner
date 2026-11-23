package com.stealaspawner.config;

import com.stealaspawner.testutil.CapturingLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import static com.stealaspawner.config.MessageManager.placeholder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageManagerTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    @TempDir
    Path dataFolder;

    private CapturingLog logs;
    private ConfigManager config;
    private MessageManager messages;

    @BeforeEach
    void setUp() throws Exception {
        logs = new CapturingLog();
        config = new ConfigManager(dataFolder, name -> getClass().getClassLoader().getResourceAsStream(name), logs.log());
        config.load();
        messages = new MessageManager(config::snapshot, logs.log());
    }

    private static boolean hasClickEvent(Component component) {
        if (component.clickEvent() != null) {
            return true;
        }
        return component.children().stream().anyMatch(child -> hasClickEvent(child));
    }

    // 10. Missing message
    @Test
    void unknownMessageReturnsAVisibleFallbackAndWarnsOnce() {
        Component first = assertDoesNotThrow(() -> messages.render("does.not.exist"));
        messages.render("does.not.exist");

        assertEquals("[missing message: does.not.exist]", PLAIN.serialize(first));
        assertEquals(1, logs.messages(Level.WARNING).stream().filter(m -> m.contains("does.not.exist")).count());
    }

    @Test
    void messageMissingFromTheFileUsesTheBuiltInDefaultWithAWarning() throws Exception {
        Path file = dataFolder.resolve("messages.yml");
        String content = Files.readString(file);
        Files.writeString(file, content.replace("  internal: \"<red>An internal error occurred. <gray>See the server console for details.\"\n", ""));
        logs.clear();

        assertTrue(config.reload().success());

        assertEquals("An internal error occurred. See the server console for details.",
                PLAIN.serialize(messages.render(MessageKey.ERROR_INTERNAL)));
        assertTrue(logs.contains(Level.WARNING, "Path: error.internal"));
    }

    // 11. Placeholder replaced
    @Test
    void placeholdersAreReplaced() {
        Component message = messages.render(MessageKey.RELOAD_SUCCESS, placeholder("files", 5), placeholder("duration", 12));

        assertEquals("Configuration reloaded (5 files, 12 ms).", PLAIN.serialize(message));
    }

    @Test
    void templatesKeepTheirMiniMessageFormatting() {
        Component message = messages.render(MessageKey.COMMAND_NO_PERMISSION);

        assertEquals(NamedTextColor.RED, message.color() != null ? message.color() : message.children().get(0).color());
    }

    // 12. Placeholder containing untrusted MiniMessage markup
    @Test
    void placeholderValuesAreNeverParsedAsMiniMessage() {
        String injected = "<click:run_command:/op attacker><red>pwned</red>";

        Component message = messages.render(MessageKey.COMMAND_UNKNOWN_SUBCOMMAND,
                placeholder("subcommand", injected), placeholder("label", "sas"));

        String text = PLAIN.serialize(message);
        assertTrue(text.contains(injected), text);
        assertFalse(hasClickEvent(message), "the injected value must not create a click event");
    }

    @Test
    void prefixIsAddedToSentMessages() {
        String prefixed = PLAIN.serialize(messages.prefixed(MessageKey.COMMAND_NO_PERMISSION));

        assertEquals("[StealASpawner] You don't have permission to use this command.", prefixed);
    }

    @Test
    void plainRenderingIsUsedForTheConsoleLog() {
        assertEquals("StealASpawner 1.2.3 enabled.", messages.plain(MessageKey.PLUGIN_ENABLED, placeholder("version", "1.2.3")));
    }

    @Test
    void clickEventsAreAbsentFromDefaultMessages() {
        assertNull(messages.render(MessageKey.RELOAD_FAILURE, placeholder("errors", 1)).clickEvent());
    }

    @Test
    void renderingUsesTheLatestSnapshot() {
        AtomicReference<ConfigurationSnapshot> ref = new AtomicReference<>(config.snapshot());
        MessageManager manager = new MessageManager(ref::get, logs.log());
        ConfigurationSnapshot current = ref.get();
        Map<MessageKey, String> changed = new EnumMap<>(current.messages());
        changed.put(MessageKey.COMMAND_NO_PERMISSION, "Denied.");
        ref.set(new ConfigurationSnapshot(current.general(), current.logging(), current.diagnostics(), changed, current.loadedAt()));

        assertEquals("Denied.", PLAIN.serialize(manager.render(MessageKey.COMMAND_NO_PERMISSION)));
    }
}
