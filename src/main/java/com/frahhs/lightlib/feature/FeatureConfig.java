package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightPlugin;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.List;

public class FeatureConfig {
    private final LightFeature feature;
    private final FeatureConfig parent;
    private final YamlFile yamlFile;

    public FeatureConfig(LightFeature feature, LightPlugin plugin, FeatureConfig parent) {
        this.feature = feature;
        this.yamlFile = plugin.getYamlConfig();
        this.parent = parent;
    }


    public FeatureConfig(LightFeature feature, LightPlugin plugin) {
        this(feature, plugin, null);
    }

    private void add(@NotNull String key, @NotNull Object defaultValue, String comment) {
        yamlFile.setComment(generatePath(key), comment);
        yamlFile.addDefault(generatePath(key), defaultValue);
    }

    private void add(@NotNull String key, @NotNull Object defaultValue) {
        yamlFile.addDefault(generatePath(key), defaultValue);
    }

    public void addString(@NotNull String key, @NotNull String defaultValue, @NotNull String comment) {
        add(key, defaultValue, comment);
    }

    public void addString(@NotNull String key, @NotNull String defaultValue) {
        add(key, defaultValue);
    }

    public void addInt(@NotNull String key, int defaultValue, @NotNull String comment) {
        add(key, defaultValue, comment);
    }

    public void addInt(@NotNull String key, int defaultValue) {
        add(key, defaultValue);
    }

    public void addBoolean(@NotNull String key, boolean defaultValue, @NotNull String comment) {
        add(key, defaultValue, comment);
    }

    public void addBoolean(@NotNull String key, boolean defaultValue) {
        add(key, defaultValue);
    }

    public void addDouble(@NotNull String key, double defaultValue, @NotNull String comment) {
        add(key, defaultValue, comment);
    }

    public void addDouble(@NotNull String key, double defaultValue) {
        add(key, defaultValue);
    }

    public void addStringList(@NotNull String key, @NotNull List<String> defaultValue, @NotNull String comment) {
        add(key, defaultValue, comment);
    }

    public void addStringList(@NotNull String key, @NotNull List<String> defaultValue) {
        add(key, defaultValue);
    }

    @NotNull
    private String generatePath(@NotNull String key) {
        System.out.println(getPath());
        return String.format("%s.%s", getPath(), key);
    }

    @NotNull
    private String getPath() {
        if(parent == null)
            return feature.getID();
        else
            return String.format("%s.%s", parent.getPath(), feature.getID());
    }
}
