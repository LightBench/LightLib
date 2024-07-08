package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightListener;
import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.item.LightItem;
import com.frahhs.lightlib.util.bag.Bag;
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
        LightPlugin.getLightLogger().fine("Registering %s feature...", feature.getID());

        // Register feature parameters
        feature.registerItems(plugin);
        feature.registerEvents(plugin);
        feature.registerBags(plugin);
        feature.registerSubFeature(plugin);

        // Enable the feature
        feature.onEnable();

        // Save the feature
        features.put(feature.getID(), feature);

        LightPlugin.getLightLogger().fine("Registered %s feature.", feature.getID());
    }

    /**
     * Unregisters a feature with the manager.
     *
     * @param feature The feature to unregister.
     */
    public void unregisterFeatures(LightFeature feature, JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering %s feature...", feature.getID());

        // Disable the feature
        feature.onDisable();

        // Unregister the feature parameters
        feature.unregisterSubFeatures(plugin);
        feature.unregisterItems(plugin);
        feature.unregisterEvents(plugin);
        feature.unregisterBags(plugin);

        // Unregister the feature
        features.remove(feature.getID());
        LightPlugin.getLightLogger().fine("Unregistered %s feature.", feature.getID());
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
                LightPlugin.getLightLogger().fine("Enabling %s feature...", feature.getID());
                feature.onEnable();
                feature.registerItems(plugin);
                LightPlugin.getLightLogger().fine("Enabled %s feature.", feature.getID());
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
                LightPlugin.getLightLogger().finer("Disabling %s feature...", feature.getID());
                feature.onDisable();
                LightPlugin.getLightLogger().finer("Disabled %s feature.", feature.getID());
            }
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("Error while disabling a feature.\nStackTrace: \n%s", e.getMessage());
        }
    }

    public void registerFeatureEvents(LightListener listener, LightFeature feature) {
        feature.eventsList.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void registerFeatureBags(Bag bag, LightFeature feature) {
        feature.bagsList.add(bag);
        LightPlugin.getBagManager().registerBags(bag);
    }

    public void registerFeatureItems(LightItem item, LightFeature feature) {
        feature.itemsList.add(item);
        LightPlugin.getItemsManager().registerItems(item, plugin);
    }

    public void registerFeatureSubFeatures(LightFeature subFeature, LightFeature feature) {
        feature.subFeatures.add(subFeature);
        registerFeatures(subFeature, plugin);
    }
}
