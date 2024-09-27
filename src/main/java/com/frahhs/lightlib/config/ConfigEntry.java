package com.frahhs.lightlib.config;

import org.simpleyaml.configuration.file.YamlFile;
import org.yaml.snakeyaml.comments.CommentType;

public class ConfigEntry {
    private final YamlFile yamlFile;
    private final String key;

    public ConfigEntry(YamlFile yamlFile, String key, Object value) {
        this.yamlFile = yamlFile;
        this.key = key;
    }

    public ConfigEntry comment(String comment) {
        yamlFile.setComment(key, comment);
        return this;
    }

    public String getComment() {
        return yamlFile.getComment(key);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return yamlFile.get(key);
    }
}
