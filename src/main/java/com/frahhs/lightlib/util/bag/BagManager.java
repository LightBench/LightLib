package com.frahhs.lightlib.util.bag;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.util.bag.exception.BagAlreadyDefinedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing bags storing plugin data.
 */
public class BagManager {
    private final Map<String, Bag> bags;

    /**
     * Constructs a BagManager.
     */
    public BagManager() {
        bags = new HashMap<>();
    }

    /**
     * Registers a bag.
     *
     * @param bag The bag to register.
     */
    public void registerBags(Bag bag) {
        LightPlugin.getLightLogger().fine("Registering bag %s...", bag.getID());

        if(bags.containsKey(bag.getID())) {
            LightPlugin.getLightLogger().error("Duplicate bag id: %s.", bag.getID());
            throw new BagAlreadyDefinedException();
        }

        bags.put(bag.getID(), bag);
        LightPlugin.getLightLogger().fine("Registered bag %s.", bag.getID());


        bag.onEnable();
        LightPlugin.getLightLogger().fine("Enabled bag %s.", bag.getID());
    }

    /**
     * Registers a bag.
     *
     * @param bag The bag to register.
     */
    public void unregisterBags(Bag bag) {
        LightPlugin.getLightLogger().fine("Unregistering bag %s...", bag.getID());

        if(!bags.containsKey(bag.getID())) {
            LightPlugin.getLightLogger().warning("Tried to unregister the bag %s,but it was not registered, please report.", bag.getID());
            return;
        }

        bag.onDisable();
        LightPlugin.getLightLogger().fine("Disabled bag %s.", bag.getID());

        bags.remove(bag.getID(), bag);
        LightPlugin.getLightLogger().fine("Unregistered bag %s.", bag.getID());
    }

    /**
     * Retrieves a bag by its ID.
     *
     * @param id The ID of the bag to retrieve.
     * @return The bag corresponding to the ID.
     */
    public Bag getBag(String id) {
        return bags.get(id);
    }

    /**
     * Retrieves a bag by its ID.
     *
     * @return The bag corresponding to the ID.
     */
    public Map<String, Bag> getRegisteredBags() {
        return new HashMap<>(bags);
    }

    /**
     * Enables all registered bags.
     */
    public void enableBags() {
        for (Bag bag : bags.values()) {
            bag.onEnable();
        }
    }

    /**
     * Disables all registered bags.
     */
    public void disableBags() {
        for (Bag bag : bags.values()) {
            bag.onDisable();
        }
    }
}