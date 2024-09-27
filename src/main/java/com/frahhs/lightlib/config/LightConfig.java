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

    public ConfigEntry add(@NotNull String key, @NotNull Object defaultValue) {
        yamlFile.addDefault(key, defaultValue);
        return new ConfigEntry(yamlFile, key, defaultValue);
    }

    public ConfigEntry set(@NotNull String key, @NotNull Object value) {
        yamlFile.set(key, value);
        return new ConfigEntry(yamlFile, key, value);
    }
}
