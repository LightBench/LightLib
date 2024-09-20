package com.frahhs.lightlib.config;

import org.simpleyaml.configuration.file.YamlFile;

public class ConfigEntry {
    private YamlFile yamlFile;
    private String key;
    private Object value;

    public ConfigEntry(YamlFile yamlFile, String key, Object value) {
        this.yamlFile = yamlFile;
        this.key = key;
        this.value = value;
    }

    public ConfigEntry comment(String comment) {
        yamlFile.setComment(key, comment);
        return this;
    }
}
