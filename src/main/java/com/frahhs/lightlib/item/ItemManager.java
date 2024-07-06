package com.frahhs.lightlib.item;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.item.exception.DuplicateIdentifierException;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class for managing custom items related to Light mechanics.
 */
public class ItemManager {
    private final JavaPlugin plugin;
    private final Map<String, LightItem> lightItems;

    /**
     * Constructor for ItemManager.
     *
     * @param plugin The JavaPlugin instance.
     */
    public ItemManager(JavaPlugin plugin) {
        this.plugin = plugin;

        // Initialize the items map
        lightItems = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new CustomRecipesListener(), plugin);
    }

    /**
     * Registers a custom LightItem.
     *
     * @param lightItem The LightItem to register.
     */
    public void registerItems(LightItem lightItem, JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Registering %s custom item...", lightItem.getName());
        if (lightItems.containsKey(lightItem.getIdentifier())) {
            throw new DuplicateIdentifierException(String.format("Duplicate item identifier [%s] found.", lightItem.getName()));
        }

        lightItems.put(lightItem.getIdentifier(), lightItem);
        lightItem.init(plugin);
        if (lightItem.isCraftable()) {
            LightPlugin.getLightLogger().fine("Adding %s shaped recipe...", lightItem.getName());
            plugin.getServer().addRecipe(lightItem.getShapedRecipe(plugin));
            LightPlugin.getLightLogger().fine("Added %s shaped recipe.", lightItem.getName());
        }
        LightPlugin.getLightLogger().fine("Registered %s custom item.", lightItem.getName());
    }

    /**
     * Dispose all registered items.
     */
    public void dispose() {
        for (String key : lightItems.keySet()) {
            LightPlugin.getLightLogger().fine("Disposing %s custom item...", lightItems.get(key).getName());
            if (lightItems.get(key).isCraftable()) {
                LightPlugin.getLightLogger().fine("Removing %s shaped recipe...", lightItems.get(key).getName());
                plugin.getServer().removeRecipe(lightItems.get(key).getNamespacedKey());
                LightPlugin.getLightLogger().fine("Removed %s shaped recipe.", lightItems.get(key).getName());
            }
            LightPlugin.getLightLogger().fine("Disposed %s custom item.", lightItems.get(key).getName());
        }
        lightItems.clear();
    }

    /**
     * Retrieves an ItemStack based on LightMaterial.
     *
     * @param itemClass The LightMaterial class of the item to retrieve.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(@NotNull Class<? extends LightItem> itemClass) {
        LightPlugin.getLightLogger().finer("Trying to get custom item by %s class...", itemClass.toString());
        for (LightItem item : lightItems.values()) {
            if (item.getClass().equals(itemClass)) {
                LightPlugin.getLightLogger().finer("Found %s custom item.", item.getName());
                return item;
            }
        }
        LightPlugin.getLightLogger().finer("Custom item with %s class not found.", itemClass.toString());
        return null;
    }

    /**
     * Retrieves an ItemStack based on LightMaterial.
     *
     * @param identifier The identifier of the item to retrieve.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(@NotNull String identifier) {
        LightPlugin.getLightLogger().finer("Trying to get custom item by %s identifier...", identifier);
        for (LightItem item : lightItems.values()) {
            if (item.getIdentifier().equals(identifier)) {
                LightPlugin.getLightLogger().finer("Found %s custom item.", item.getName());
                return item;
            }
        }
        LightPlugin.getLightLogger().finer("Custom item with %s identifier not found.", identifier);
        return null;
    }

    /**
     * Retrieves a LightItem based on the provided ItemStack.
     *
     * @param itemStack The ItemStack to match.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(ItemStack itemStack) {
        LightPlugin.getLightLogger().finer("Trying to get custom item by %s ItemStack...", itemStack);
        if(itemStack == null) {
            LightPlugin.getLightLogger().finer("Failed to get custom item, null ItemStack.");
            return null;
        }

        if(itemStack.getType().equals(Material.AIR)) {
            LightPlugin.getLightLogger().finer("Failed to get custom item, air ItemStack.");
            return null;
        }

        for (LightItem curItem : lightItems.values()) {
            ItemStack item1 = clean(itemStack);
            ItemStack item2 = clean(curItem.getItemStack());
            if(item1.isSimilar(item2)) {
                LightPlugin.getLightLogger().finer("Found %s custom item.", curItem.getName());
                return curItem;
            }
        }
        LightPlugin.getLightLogger().finer("Custom item with %s ItemStack not found.", itemStack);
        return null;
    }


    /**
     * Retrieves all registered LightItem.
     *
     * @return A collection of all registered LightItem.
     */
    public Set<LightItem> getRegisteredItems() {
        return new HashSet<>(lightItems.values());
    }

    /**
     * Checks if the provided ItemStack is registered as a custom item.
     *
     * @param itemStack The ItemStack to check.
     * @return True if the ItemStack is registered, otherwise false.
     */
    public boolean isRegistered(ItemStack itemStack) {
        if(itemStack == null)
            return false;

        ItemStack item = clean(itemStack);

        for (LightItem curItem : lightItems.values()) {
            ItemStack item2 = clean(curItem.getItemStack());
            if (item2.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }

    // return a cloned clean item
    private ItemStack clean(ItemStack itemStack) {
        ItemStack item = itemStack.clone();

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        // Remove PersistentDataContainer keys from the ItemStack
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for(NamespacedKey key : container.getKeys())
            container.remove(key);

        // Remove Lore the ItemStack
        meta.setLore(null);

        item.setItemMeta(meta);
        return item;
    }
}
