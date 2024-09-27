package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightListener;
import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.item.LightItem;
import com.frahhs.lightlib.util.bag.Bag;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a feature in the plugin.
 */
public abstract class LightFeature {
    List<LightListener> eventsList = new ArrayList<>();
    List<Bag> bagsList = new ArrayList<>();
    List<LightItem> itemsList = new ArrayList<>();
    List<LightFeature> subFeatures = new ArrayList<>();

    FeatureConfig config;

    /**
     * Called when the feature is loaded.
     */
    protected void onLoad(LightPlugin plugin) {}

    /**
     * Called when the feature is enabled.
     */
    protected abstract void onEnable(LightPlugin plugin);

    /**
     * Called when the feature is disabled.
     */
    protected abstract void onDisable(LightPlugin plugin);

    /**
     * Handle configuration.
     */
    protected FeatureConfig getConfig() {
        return config;
    }

    /**
     * Retrieves the ID of the feature.
     *
     * @return The ID of the feature.
     */
    @NotNull
    protected abstract String getID();

    void setFeatureConfig(FeatureConfig featureConfig) {
        this.config = featureConfig;
    }

    FeatureConfig getFeatureConfig() {
        return config;
    }

    void unregisterSubFeatures(LightPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering sub features for the feature %s...", getID());

        for(LightFeature subFeature : subFeatures) {
            plugin.getFeatureManager().registerFeatures(subFeature, plugin);
        }

        LightPlugin.getLightLogger().fine("Unregistered sub features for the feature %s.", getID());
    }

    void unregisterItems(LightPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering items for the feature %s...", getID());

        for(LightItem item : itemsList) {
            plugin.getItemsManager().unregisterItems(item, plugin);
        }

        LightPlugin.getLightLogger().fine("Unregistered items for the feature %s.", getID());
    }

    void unregisterBags(LightPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering bags for the feature %s...", getID());

        for(Bag bag : bagsList) {
            plugin.getBagManager().unregisterBags(bag);
        }

        LightPlugin.getLightLogger().fine("Unregistered bags for the feature %s.", getID());
    }

    void unregisterEvents(LightPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering listeners for the feature %s...", getID());

        for(LightListener event : eventsList) {
            HandlerList.unregisterAll(event);
        }

        LightPlugin.getLightLogger().fine("Unregistered listeners for the feature %s.", getID());
    }
}
