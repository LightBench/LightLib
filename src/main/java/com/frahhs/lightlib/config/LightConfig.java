package com.frahhs.lightlib.config;

import com.frahhs.lightlib.LightPlugin;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Manages the configurations of the plugin.
 */
public class LightConfig {
    static YamlFile yamlFile;

    public LightConfig(LightPlugin plugin) {

        yamlFile = new YamlFile( plugin.getDataFolder() + File.separator + "config.yml");

        try {
            if (!yamlFile.exists()) {
                yamlFile.createNewFile();
            }
        } catch (final Exception e) {
            LightPlugin.getLightLogger().error("Error while creating config.yml file.\n%s", e);
        }
    }

    public void read() {
        // Load the YAML file if is already created or create new one otherwise
        try {
            yamlFile.loadWithComments(); // Loads the entire file
        } catch (final Exception e) {
            LightPlugin.getLightLogger().error("Error while reading config.yml file.\n%s", e);
        }
    }

    public void save() {
        try {
            yamlFile.save();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public YamlFile getConfig() {
        return yamlFile;
    }

/*    protected ConfigEntry add(@NotNull String key, @NotNull Object defaultValue, String comment) {
        yamlFile.setComment(key, comment);
        yamlFile.addDefault(key, defaultValue);
    }*/

    public ConfigEntry add(@NotNull String key, @NotNull Object defaultValue) {
        yamlFile.addDefault(key, defaultValue);
        return new ConfigEntry(yamlFile, key, defaultValue);
    }

    public ConfigEntry addString(@NotNull String key, @NotNull String defaultValue) {
        return add(key, defaultValue);
    }

    public ConfigEntry addInt(@NotNull String key, int defaultValue) {
        return add(key, defaultValue);
    }

    public ConfigEntry addBoolean(@NotNull String key, boolean defaultValue) {
        return add(key, defaultValue);
    }

    public ConfigEntry addDouble(@NotNull String key, double defaultValue) {
        return add(key, defaultValue);
    }

    public ConfigEntry addStringList(@NotNull String key, @NotNull List<String> defaultValue) {
        return add(key, defaultValue);
    }
}
