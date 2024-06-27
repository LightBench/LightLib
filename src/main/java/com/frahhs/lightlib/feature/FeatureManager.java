package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for managing features within the plugin.
 */
public class FeatureManager {
    protected LightPlugin plugin;
    private final Map<String, LightFeature> features;

    /**
     * Constructs a FeatureManager object.
     *
     * @param plugin The main plugin instance.
     */
    public FeatureManager(LightPlugin plugin) {
        this.plugin = plugin;
        features = new HashMap<>();
    }

    /**
     * Registers a feature with the manager.
     *
     * @param feature The feature to register.
     */
    public void registerFeatures(LightFeature feature, JavaPlugin plugin) {
        features.put(feature.getID(), feature);
        feature.registerItems(plugin);
        feature.registerEvents(plugin);
        feature.registerBags(plugin);
        feature.onEnable();
    }

    /**
     * Retrieves a feature by its ID.
     *
     * @param id The ID of the feature.
     * @return The feature with the given ID, or null if not found.
     */
    public LightFeature getFeature(String id) {
        return features.get(id);
    }

    /**
     * Enables all registered features.
     */
    public void enableFeatures() {
        try {
            for (LightFeature feature : features.values()) {
                LightPlugin.getLightLogger().finer("Enabling %s feature", feature.getID());
                feature.onEnable();
            }
        } catch (Error e) {
            LightPlugin.getLightLogger().error("Error while enabling a feature.\nStackTrace: \n%s", e.getMessage());
        }
    }

    /**
     * Disables all registered features.
     */
    public void disableFeatures() {
        try {
            for (LightFeature feature : features.values()) {
                LightPlugin.getLightLogger().finer("Disabling %s feature", feature.getID());
                feature.onDisable();
            }
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("Error while disabling a feature.\nStackTrace: \n%s", e.getMessage());
        }
    }
}
