package com.frahhs.lightlib.item;

import com.frahhs.lightlib.LightObject;
import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.util.recipe.RecipeManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Abstract class representing a custom Light item.
 */
public abstract class LightItem extends LightObject {
    protected NamespacedKey namespacedKey;
    protected ItemStack item;

    /**
     * Init for LightItem.
     */
    protected void init() {
        this.namespacedKey = new NamespacedKey(plugin, getIdentifier());

        item = new ItemStack(getVanillaMaterial(), 1);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        // Display name
        meta.setDisplayName(ChatColor.WHITE + getName());

        // Lore
        if(getLore() != null) {
            meta.setLore(getLore());
        }

        // Custom model data
        meta.setCustomModelData(getCustomModelData());

        item.setItemMeta(meta);
    }

    /**
     * Retrieves the ItemStack of the custom Light item.
     *
     * @return The ItemStack of the custom Light item.
     */
    public ItemStack getItemStack() {
        return item.clone();
    }

    /**
     * Retrieve the name of the custom Light item from the lang file.
     */
    public String getName() {
        return messages.getMessage("items_name."  + getIdentifier(), false);
    }

    /**
     * Retrieve the identifier of the custom item, it coincides with the item_name in the lang file.
     * The name must be unique, it must be composed by alphanumeric and underscores.
     */
    @NotNull
    public abstract String getIdentifier();

    /**
     * Retrieves the Lore of the custom Light item.
     *
     * @return The List<String> of the Lore of the custom Light item, null if it has no lore.
     */
    public List<String> getLore() {
        return null;
    }

    /**
     * Retrieves the name of the custom Light item.
     */
    public abstract int getCustomModelData();

    /**
     * Retrieves the saved shaped recipe of the custom Light item.
     * If the plugin fail to select it, default shaped recipe will be returned
     *
     * @return The shaped recipe of the custom Light item.
     */
    public ShapedRecipe getShapedRecipe(JavaPlugin plugin) {
        RecipeManager recipeManager = new RecipeManager(plugin);

        ShapedRecipe shapedRecipe = getDefaultShapedRecipe();

        try {
            shapedRecipe = recipeManager.loadRecipe(this);
        } catch (SQLException e) {
            LightPlugin.getLightLogger().error("Error while selecting the recipe of %s from the database\n%s", getName(), e);
        }

        if(shapedRecipe == null)
            return getDefaultShapedRecipe();

        return shapedRecipe;
    }

    public void updateShapedRecipe(ShapedRecipe shapedRecipe, JavaPlugin plugin) {
        if(!isCraftable()) {
            LightPlugin.getLightLogger().warning("Trying to update the recipe of a non craftable item: %s", getName());
            return;
        }

        RecipeManager recipeManager = new RecipeManager(plugin);

        try {
            recipeManager.saveRecipe(this, shapedRecipe);
        } catch (SQLException e) {
            LightPlugin.getLightLogger().error("Error while saving the recipe of %s from the database\n%s", getName(), e);
        }

        plugin.getServer().removeRecipe(getNamespacedKey());
        plugin.getServer().addRecipe(shapedRecipe);
    }

    /**
     * Retrieves the namespaced key of the custom Light item.
     *
     * @return The namespaced key of the custom Light item.
     */
    @NotNull
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    /**
     * Retrieves if the custom Light item is craftable.
     */
    public boolean isCraftable() {
        return getDefaultShapedRecipe() != null;
    }

    /**
     * Retrieves the default shaped recipe of the custom Light item.
     *
     * @return The shaped recipe of the custom Light item.
     */
    public abstract ShapedRecipe getDefaultShapedRecipe();

    /**
     * Retrieves if the custom Light item can be given.
     */
    public abstract boolean isGivable();

    /**
     * Retrieves the name of the custom Light item.
     */
    @NotNull
    public abstract Material getVanillaMaterial();
}
