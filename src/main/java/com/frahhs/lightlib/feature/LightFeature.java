package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightListener;
import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.item.LightItem;
import com.frahhs.lightlib.util.bag.Bag;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a feature in the plugin.
 */
public abstract class LightFeature {
    protected List<LightListener> eventsList = new ArrayList<>();
    protected List<Bag> bagsList = new ArrayList<>();
    protected List<LightItem> itemsList = new ArrayList<>();
    protected List<LightFeature> subFeatures = new ArrayList<>();

    /**
     * Called when the feature is enabled.
     */
    protected abstract void onEnable();

    /**
     * Called when the feature is disabled.
     */
    protected abstract void onDisable();

    /**
     * Registers sub features for the current feature.
     * <p>
     * The sub feature should extend the parent feature in order to
     * correctly handle the feature life.
     */
    protected abstract void registerSubFeature(JavaPlugin plugin);

    /**
     * Registers items for the feature.
     */
    protected abstract void registerItems(JavaPlugin plugin);

    /**
     * Registers bags for the feature.
     */
    protected abstract void registerBags(JavaPlugin plugin);

    /**
     * Registers events for the feature.
     */
    protected abstract void registerEvents(JavaPlugin plugin);

    /**
     * Retrieves the ID of the feature.
     *
     * @return The ID of the feature.
     */
    @NotNull
    protected abstract String getID();

    void unregisterSubFeatures(JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering sub features for the feature %s...", getID());

        for(LightFeature subFeature : subFeatures) {
            LightPlugin.getFeatureManager().registerFeatures(subFeature, plugin);
        }

        LightPlugin.getLightLogger().fine("Unregistered sub features for the feature %s.", getID());
    }

    void unregisterItems(JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering items for the feature %s...", getID());

        for(LightItem item : itemsList) {
            LightPlugin.getItemsManager().unregisterItems(item, plugin);
        }

        LightPlugin.getLightLogger().fine("Unregistered items for the feature %s.", getID());
    }

    void unregisterBags(JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering bags for the feature %s...", getID());

        for(Bag bag : bagsList) {
            LightPlugin.getBagManager().unregisterBags(bag);
        }

        LightPlugin.getLightLogger().fine("Unregistered bags for the feature %s.", getID());
    }

    void unregisterEvents(JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering listeners for the feature %s...", getID());

        for(LightListener event : eventsList) {
            HandlerList.unregisterAll(event);
        }

        LightPlugin.getLightLogger().fine("Unregistered listeners for the feature %s.", getID());
    }
}
