package com.stealaspawner.config;

import com.stealaspawner.logging.PluginLog;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Renders the messages of {@code messages.yml} with MiniMessage.
 *
 * <ul>
 *   <li>Templates and the prefix are MiniMessage strings written by the server admin (trusted).</li>
 *   <li>Placeholder values are inserted with {@link Placeholder#unparsed(String, String)}: they are
 *       always displayed as plain text, so a value such as {@code <click:run_command:/op me>} can
 *       never be interpreted as markup.</li>
 *   <li>A missing message never throws: a visible fallback is returned and a warning is logged once.</li>
 * </ul>
 */
public final class MessageManager {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private final Supplier<ConfigurationSnapshot> snapshots;
    private final PluginLog log;
    private final Set<String> reportedMissing = ConcurrentHashMap.newKeySet();

    public MessageManager(Supplier<ConfigurationSnapshot> snapshots, PluginLog log) {
        this.snapshots = Objects.requireNonNull(snapshots, "snapshots");
        this.log = Objects.requireNonNull(log, "log");
    }

    /** Safe placeholder: {@code <name>} in a template is replaced by {@code value} as plain text. */
    public static TagResolver placeholder(String name, Object value) {
        return Placeholder.unparsed(name, String.valueOf(value));
    }

    /** Message body without prefix. */
    public Component render(MessageKey key, TagResolver... placeholders) {
        return parse(key.path(), snapshots.get().message(key), placeholders);
    }

    /**
     * Message body looked up by its YAML path. Unknown paths return a visible fallback
     * ({@code [missing message: path]}) and log a warning once, instead of failing.
     */
    public Component render(String path, TagResolver... placeholders) {
        return MessageKey.byPath(path)
                .map(key -> render(key, placeholders))
                .orElseGet(() -> missing(path));
    }

    /** Message with the configured prefix, as sent to players and the console. */
    public Component prefixed(MessageKey key, TagResolver... placeholders) {
        Component body = render(key, placeholders);
        String prefix = snapshots.get().general().prefix();
        if (prefix.isEmpty()) {
            return body;
        }
        return Component.empty().append(parse("general.prefix", prefix)).append(body);
    }

    /** Sends a prefixed message to a player, the console or any other audience. */
    public void send(Audience audience, MessageKey key, TagResolver... placeholders) {
        audience.sendMessage(prefixed(key, placeholders));
    }

    /** Plain-text message body, for server logs. */
    public String plain(MessageKey key, TagResolver... placeholders) {
        return PLAIN.serialize(render(key, placeholders));
    }

    private Component parse(String path, String template, TagResolver... placeholders) {
        try {
            return MINI_MESSAGE.deserialize(template, TagResolver.resolver(placeholders));
        } catch (RuntimeException e) {
            if (reportedMissing.add("invalid:" + path)) {
                log.warning("Message " + path + " cannot be parsed as MiniMessage (" + e.getMessage()
                        + "). It is displayed as plain text.");
            }
            return Component.text(template);
        }
    }

    private Component missing(String path) {
        if (reportedMissing.add(path)) {
            log.warning("Message " + path + " does not exist in messages.yml.");
        }
        return Component.text("[missing message: " + path + "]");
    }
}
