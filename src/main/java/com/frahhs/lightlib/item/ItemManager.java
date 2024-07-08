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

        // Register the listener for the crafting permissions checking
        plugin.getServer().getPluginManager().registerEvents(new CustomRecipesListener(), plugin);
    }

    /**
     * Registers a custom LightItem.
     *
     * @param lightItem The LightItem to register.
     */
    public void registerItems(LightItem lightItem, JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Registering %s custom item...", lightItem.getName());

        // Check if the identifier is already declared
        if (lightItems.containsKey(lightItem.getIdentifier())) {
            throw new DuplicateIdentifierException(String.format("Duplicate item identifier [%s] found.", lightItem.getName()));
        }

        // Init the item
        lightItem.init(plugin);

        // Create the item recipe
        if (lightItem.isCraftable()) {
            LightPlugin.getLightLogger().fine("Adding %s shaped recipe...", lightItem.getName());
            plugin.getServer().addRecipe(lightItem.getShapedRecipe(plugin));
            LightPlugin.getLightLogger().fine("Added %s shaped recipe.", lightItem.getName());
        }

        // Save the item
        lightItems.put(lightItem.getIdentifier(), lightItem);

        LightPlugin.getLightLogger().fine("Registered %s custom item.", lightItem.getName());
    }

    /**
     * Unregisters a custom LightItem.
     *
     * @param lightItem The LightItem to unregister.
     */
    public void unregisterItems(LightItem lightItem, JavaPlugin plugin) {
        LightPlugin.getLightLogger().fine("Unregistering %s custom item...", lightItem.getName());

        // Check if the item is registered
        if (!lightItems.containsKey(lightItem.getIdentifier())) {
            LightPlugin.getLightLogger().warning("Tried to unregister the item %s,but it was not registered, please report.", lightItem.getName());
            return;
        }

        // Remove the recipe
        if (lightItem.isCraftable()) {
            LightPlugin.getLightLogger().fine("Removing %s shaped recipe...", lightItem.getName());
            plugin.getServer().removeRecipe(lightItem.getNamespacedKey());
            LightPlugin.getLightLogger().fine("Removed %s shaped recipe.", lightItem.getName());
        }

        // Remove the item
        lightItems.remove(lightItem.getIdentifier());

        LightPlugin.getLightLogger().fine("Unregistered %s custom item.", lightItem.getName());
    }

    /**
     * Dispose all registered items.
     */
    public void dispose() {
        LightPlugin.getLightLogger().fine("Disposing all custom items...");

        // Unregister all known items one by one
        for (String key : lightItems.keySet()) {
            unregisterItems(lightItems.get(key), plugin);
        }

        LightPlugin.getLightLogger().fine("Disposed all custom items.");
    }

    /**
     * Retrieves an ItemStack based on the given class.
     * <p>
     * This method should not be used anymore since multiple instance of the same Object can be declared.
     *
     * @param itemClass The LightMaterial class of the item to retrieve.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    @Deprecated
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
     * Retrieves an ItemStack based on the identifier.
     *
     * @param identifier The identifier of the item to retrieve.
     * @return The corresponding LightItem, or null if no matching LightItem is found.
     */
    public LightItem get(@NotNull String identifier) {
        LightPlugin.getLightLogger().finer("Trying to get custom item by %s identifier...", identifier);

        // Search for a matching identifier in the registered items list.
        for (LightItem item : lightItems.values()) {

            // If a matching identifier is found return the corresponding LightItem.
            if (item.getIdentifier().equals(identifier)) {
                LightPlugin.getLightLogger().finer("Found %s custom item.", item.getName());
                return item;
            }
        }

        // If no matching identifier is found return null
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

        // Check if the ItemStack is null, if it is, return null
        if(itemStack == null) {
            LightPlugin.getLightLogger().finer("Failed to get custom item, null ItemStack.");
            return null;
        }

        // Check if the ItemStack is AIR
        if(itemStack.getType().equals(Material.AIR)) {
            LightPlugin.getLightLogger().finer("Failed to get custom item, air ItemStack.");
            return null;
        }

        // Search for a matching ItemStack in the registered items list.
        for (LightItem curItem : lightItems.values()) {
            ItemStack item1 = clean(itemStack);
            ItemStack item2 = clean(curItem.getItemStack());

            // If a matching ItemStack is found return the corresponding LightItem.
            if(item1.isSimilar(item2)) {
                LightPlugin.getLightLogger().finer("Found %s custom item.", curItem.getName());
                return curItem;
            }
        }

        // If no matching ItemStack is found return null
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
