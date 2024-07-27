package com.frahhs.lightlib.provider;

import com.frahhs.lightlib.LightPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

/**
 * Manages the configurations of the plugin.
 */
public class ConfigProvider {
    static YamlFile yamlFile;

    public ConfigProvider(LightPlugin plugin) {

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
}
