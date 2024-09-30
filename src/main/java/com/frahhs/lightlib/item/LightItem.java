package com.frahhs.lightlib.item;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.util.recipe.RecipeManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class representing a custom Light item.
 */
public abstract class LightItem {
    private LightPlugin plugin;
    protected NamespacedKey namespacedKey;
    protected ItemStack item;
    YamlFile configuration;

    /**
     * Init for LightItem.
     */
    protected void init(LightPlugin plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, getIdentifier());

        item = new ItemStack(getMaterial(), 1);

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
        ItemStack ItemStack = item.clone();

        ItemMeta meta = ItemStack.getItemMeta();
        assert meta != null;

        // UUID
        if(isUnique()) {
            NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");
            meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        }

        ItemStack.setItemMeta(meta);

        return ItemStack;
    }

    /**
     * Retrieve the default name of the custom Light item from the lang file.
     */
    public abstract String getDefaultName();

    /**
     * Retrieve the name of the custom Light item from the lang file.
     */
    public String getName() {
        if(configuration.getString("display-name") == null)
            return getDefaultName();

        return configuration.getString("display-name");
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
     * Retrieves the default custom model data of the custom Light item.
     */
    public abstract int getDefaultCustomModelData();

    /**
     * Retrieves the custom model data of the custom Light item.
     */
    public int getCustomModelData() {
        if(configuration.get("custom-model-data") == null)
            return getDefaultCustomModelData();

        return configuration.getInt("custom-model-data");
    }

    /**
     * Retrieves the default shaped recipe of the custom Light item.
     *
     * @return The shaped recipe of the custom Light item.
     */
    public abstract ShapedRecipe getDefaultShapedRecipe();

    /**
     * Retrieves the saved shaped recipe of the custom Light item.
     * If the plugin fail to select it, default shaped recipe will be returned
     *
     * @return The shaped recipe of the custom Light item.
     */
    public ShapedRecipe getShapedRecipe(LightPlugin plugin) {
        RecipeManager recipeManager = new RecipeManager(plugin);

        ShapedRecipe shapedRecipe = getDefaultShapedRecipe();

        try {
            shapedRecipe = recipeManager.loadRecipe(this);
        } catch (SQLException e) {
            LightPlugin.getLightLogger().error("Error while selecting the recipe of %s from the database\n%s", getName(), e);
        }

        if(shapedRecipe == null) {
            LightPlugin.getLightLogger().fine("Using default recipe for item %s.", getName());
            return getDefaultShapedRecipe();
        }

        return shapedRecipe;
    }

    public void updateShapedRecipe(ShapedRecipe shapedRecipe, LightPlugin plugin) {
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
     * Retrieves if the custom Light item can be given.
     */
    public abstract boolean isGivable();

    /**
     * Retrieves if the custom Light item is unique.
     */
    public abstract boolean isUnique();

    /**
     * Retrieves the default material of the custom Light item.
     */
    @NotNull
    public abstract Material getDefaultMaterial();

    /**
     * Retrieves the name of the custom Light item.
     */
    @NotNull
    public Material getMaterial() {
        String materialString = configuration.getString("material");
        if(materialString == null)
            return getDefaultMaterial();

        Material material = Material.getMaterial(materialString);

        if(material == null) {
            LightPlugin.getLightLogger().error("The custom item %s have an invalid material: %s.", getIdentifier(), materialString);
            return getDefaultMaterial();
        }

        return material;
    }

    void configure(LightPlugin plugin) {
        // Create new YAML file with relative path
        String uri = plugin.getDataFolder() +
                File.separator         +
                "items"                +
                File.separator         +
                getIdentifier()        +
                ".yml"                 ;

        configuration = new YamlFile(uri);

        try {
            if (!configuration.exists()) {
                configuration.createNewFile();
            }
            configuration.load();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        configuration.addDefault("display-name", getDefaultName());
        configuration.addDefault("material", getDefaultMaterial().toString());
        configuration.addDefault("custom-model-data", getDefaultCustomModelData());

        try {
            configuration.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
