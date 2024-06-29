package com.frahhs.lightlib.provider;

import com.frahhs.lightlib.LightPlugin;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the configurations of the plugin.
 */
public class ConfigProvider {
    private final JavaPlugin plugin;

    private Map<String, Object> config;

    /**
     * Constructs a new ConfigManager instance.
     *
     * @param plugin The JavaPlugin instance owning this manager.
     */
    public ConfigProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();

        // Read and store config values
        readConfig();
    }

    /**
     * Reads the configuration from the config.yml file and populates the config map.
     */
    private void readConfig() {
        config = new HashMap<>();
        this.plugin.reloadConfig();
        FileConfiguration configFile = this.plugin.getConfig();
        configFile.options().copyDefaults(true);
        this.plugin.saveConfig();

        // Iterate through all keys in config
        for (String key : configFile.getKeys(true)) {
            Object value = configFile.get(key);

            // Add non-null values to config map
            if (!(value instanceof MemorySection))
                config.put(key, value);
        }
    }

    /**
     * Reloads the configuration from the config.yml file.
     */
    public void reload() {
        readConfig();
    }

    /**
     * Edits a value in the configuration and saves it to the config.yml file.
     *
     * @param key      The key of the value to be edited.
     * @param newValue The new value to be set.
     * @throws RuntimeException If the key does not exist or the new value has an incompatible type.
     */
    public void editConfig(String key, Object newValue) {
        FileConfiguration configFile = plugin.getConfig();

        Object oldValue = configFile.get(key);
        if (oldValue == null)
            throw new RuntimeException(String.format("The key '%s' is not present in the config.yml file", key));
        if (!oldValue.getClass().equals(newValue.getClass()))
            throw new RuntimeException(String.format("The class of '%s' must be the same as '%s', not '%s'", key, oldValue.getClass(), newValue.getClass()));

        // Update config map and save to file
        config.put(key, newValue);
        configFile.set(key, newValue);
        plugin.saveConfig();
    }

    /**
     * Retrieves the value associated with the given path from the config map.
     *
     * @param path The path of the value to retrieve.
     * @return The value associated with the given path.
     */
    public Object get(String path) {
        return config.get(path);
    }

    /**
     * Checks if the given path exists in the config map.
     *
     * @param path The path to check.
     * @return True if the path exists, otherwise false.
     */
    public boolean pathExist(String path) {
        return config.containsKey(path);
    }

    /**
     * Retrieves the boolean value associated with the given path from the config map.
     *
     * @param path The path of the value to retrieve.
     * @return The boolean value associated with the given path.
     * @throws ClassCastException If the value at the given path is not a boolean.
     */
    public Boolean getBoolean(String path) {
        if (!pathExist(path))
            LightPlugin.getLightLogger().error(String.format("The config path '%s' does not exist.", path));

        Object value = config.get(path);
        if (!(value instanceof Boolean))
            throw new ClassCastException(String.format("Value at '%s' is not a Boolean but is a %s.", path, value.getClass().toString()));
        return (Boolean) value;
    }

    /**
     * Retrieves the integer value associated with the given path from the config map.
     *
     * @param path The path of the value to retrieve.
     * @return The integer value associated with the given path.
     * @throws ClassCastException If the value at the given path is not an integer.
     */
    public int getInt(String path) {
        if (!pathExist(path))
            LightPlugin.getLightLogger().error(String.format("The config path '%s' does not exist.", path));

        Object value = config.get(path);
        if (!(value instanceof Integer))
            throw new ClassCastException(String.format("Value at '%s' is not an Integer but is a %s.", path, value.getClass().toString()));
        return (int) value;
    }

    /**
     * Retrieves the integer value associated with the given path from the config map.
     *
     * @param path The path of the value to retrieve.
     * @return The integer value associated with the given path.
     * @throws ClassCastException If the value at the given path is not an integer.
     */
    public double getDouble(String path) {
        if (!pathExist(path))
            LightPlugin.getLightLogger().error(String.format("The config path '%s' does not exist.", path));

        Object value = config.get(path);
        if (!(value instanceof Double))
            throw new ClassCastException(String.format("Value at '%s' is not an Double but is a %s.", path, value.getClass().toString()));
        return (double) value;
    }

    /**
     * Retrieves the string value associated with the given path from the config map.
     *
     * @param path The path of the value to retrieve.
     * @return The string value associated with the given path.
     * @throws ClassCastException If the value at the given path is not a string.
     */
    public String getString(String path) {
        if (!pathExist(path))
            LightPlugin.getLightLogger().error(String.format("The config path '%s' does not exist.", path));

        Object value = config.get(path);
        if (!(value instanceof String))
            throw new ClassCastException(String.format("Value at '%s' is not a String but is a %s.", path, value.getClass().toString()));
        return (String) value;
    }

    /**
     * Retrieves the list of strings associated with the given path from the config map.
     *
     * @param path The path of the value to retrieve.
     * @return The list of strings associated with the given path.
     * @throws ClassCastException If the value at the given path is not a list of strings.
     */
    public List<String> getStringList(String path) {
        if (!pathExist(path))
            LightPlugin.getLightLogger().error(String.format("The config path '%s' does not exist.", path));

        Object value = config.get(path);
        if (!(value instanceof List))
            throw new ClassCastException(String.format("Value at '%s' is not a List but is a %s.", path, value.getClass().toString()));
        return (List<String>) value;
    }
}
