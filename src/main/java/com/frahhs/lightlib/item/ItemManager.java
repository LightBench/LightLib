package com.frahhs.lightlib.item;

import com.frahhs.lightlib.LightPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

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
        if (lightItems.containsKey(lightItem.getIdentifier())) {
            throw new RuntimeException(String.format("Item name [%s] already exists.", lightItem.getName()));
        }

        lightItems.put(lightItem.getIdentifier(), lightItem);
        lightItem.init(plugin);
        if (lightItem.isCraftable()) {
            LightPlugin.getLightLogger().finer("Adding %s shaped recipe.", lightItem.getName());
            plugin.getServer().addRecipe(lightItem.getShapedRecipe(plugin));
        }
    }

    /**
     * Dispose all registered items.
     */
    public void dispose() {
        for (String key : lightItems.keySet()) {
            if (lightItems.get(key).isCraftable()) {
                LightPlugin.getLightLogger().finer("Removing %s shaped recipe.", lightItems.get(key).getName());
                plugin.getServer().removeRecipe(lightItems.get(key).getNamespacedKey());
            }
        }
        lightItems.clear();
    }

    /**
     * Retrieves an ItemStack based on LightMaterial.
     *
     * @param itemClass The LightMaterial class of the item to retrieve.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(Class<? extends LightItem> itemClass) {
        for (LightItem item : lightItems.values()) {
            if (item.getClass().equals(itemClass)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Retrieves an ItemStack based on LightMaterial.
     *
     * @param identifier The identifier of the item to retrieve.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(String identifier) {
        for (LightItem curItem : lightItems.values()) {
            if (curItem.getIdentifier().equals(identifier)) {
                return curItem;
            }
        }
        return null;
    }

    /**
     * Retrieves a LightItem based on the provided ItemStack.
     *
     * @param itemStack The ItemStack to match.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(ItemStack itemStack) {
        if(itemStack == null)
            return null;

        if(itemStack.getType().equals(Material.AIR))
            return null;

        for (LightItem curItem : lightItems.values()) {
            ItemStack item1 = clean(itemStack);
            ItemStack item2 = clean(curItem.getItemStack());
            if(item1.isSimilar(item2)) {
                return curItem;
            }
        }
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
