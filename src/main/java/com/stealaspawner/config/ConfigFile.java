package com.stealaspawner.config;

/** Every YAML file managed by the plugin, in loading order. */
public enum ConfigFile {
    /** General settings. */
    CONFIG("config.yml"),
    /** Player and console messages. */
    MESSAGES("messages.yml"),
    /** Reserved for the future spawner system (no setting yet). */
    SPAWNERS("spawners.yml"),
    /** Reserved for the future economy system (no setting yet). */
    ECONOMY("economy.yml"),
    /** Reserved for the future collector system (no setting yet). */
    COLLECTORS("collectors.yml");

    /** Format version supported by this plugin build, stored in {@code config-version}. */
    public static final int SUPPORTED_VERSION = 1;

    private final String fileName;

    ConfigFile(String fileName) {
        this.fileName = fileName;
    }

    public String fileName() {
        return fileName;
    }
}
