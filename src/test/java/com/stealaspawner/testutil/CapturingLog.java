package com.stealaspawner.testutil;

import com.stealaspawner.logging.PluginLog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** A {@link PluginLog} backed by an isolated logger that records every message. */
public final class CapturingLog {

    private final List<LogRecord> records = new CopyOnWriteArrayList<>();
    private final PluginLog log;

    public CapturingLog() {
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record) && logger.isLoggable(record.getLevel())) {
                    records.add(record);
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
        this.log = new PluginLog(logger);
    }

    public PluginLog log() {
        return log;
    }

    public List<String> messages(Level level) {
        return records.stream().filter(r -> r.getLevel().equals(level)).map(LogRecord::getMessage).toList();
    }

    public boolean contains(Level level, String fragment) {
        return messages(level).stream().anyMatch(m -> m.contains(fragment));
    }

    public void clear() {
        records.clear();
    }
}
