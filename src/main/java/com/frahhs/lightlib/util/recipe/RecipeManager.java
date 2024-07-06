package com.frahhs.lightlib.util.recipe;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.item.LightItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RecipeManager {
    private JavaPlugin plugin;
    private Connection connection;

    public RecipeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.connection = LightPlugin.getLightDatabase().getConnection();
    }

    public void saveRecipe(LightItem item, ShapedRecipe recipe) throws SQLException {
        LightPlugin.getLightLogger().fine("Saving %s shaped recipe...", item.getName());

        String sql = "REPLACE INTO ShapedRecipe (item, pattern, ingredients) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, item.getName());
        pstmt.setString(2, String.join(";", recipe.getShape()));

        StringBuilder ingredients = new StringBuilder();
        for (Map.Entry<Character, ItemStack> entry : recipe.getIngredientMap().entrySet()) {
            if (entry.getValue() != null) {
                ingredients .append(entry.getKey()).append(":")
                            .append(entry.getValue().getType()).append(";");
            }
        }
        pstmt.setString(3, ingredients.toString());
        pstmt.executeUpdate();
        pstmt.close();
        LightPlugin.getLightLogger().fine("Saved %s shaped recipe!", item.getName());
    }

    public ShapedRecipe loadRecipe(LightItem item) throws SQLException {
        LightPlugin.getLightLogger().fine("Loading %s shaped recipe...", item.getName());

        String sql = "SELECT * FROM ShapedRecipe WHERE item = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, item.getName());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            // Deserialize the pattern and ingredients
            String[] shape = rs.getString("pattern").split(";");
            Map<Character, ItemStack> ingredientMap = new HashMap<>();
            String[] ingredients = rs.getString("ingredients").split(";");
            for (String ingredient : ingredients) {
                String[] entry = ingredient.split(":");
                ingredientMap.put(entry[0].charAt(0), new ItemStack(Material.matchMaterial(entry[1])));
            }

            // Now construct the NamespacedKey for the recipe
            NamespacedKey key = item.getNamespacedKey();

            // Create the ShapedRecipe and set its components
            ShapedRecipe recipe = new ShapedRecipe(key, item.getItemStack());
            recipe.shape(shape);
            for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
                if (entry.getValue() != null) {
                    recipe.setIngredient(entry.getKey(), entry.getValue().getType());
                }
            }

            // Close resources
            rs.close();
            pstmt.close();
            LightPlugin.getLightLogger().fine("Loaded %s shaped recipe!", item.getName());
            return recipe;
        }

        // Close resources
        rs.close();
        pstmt.close();
        LightPlugin.getLightLogger().fine("shaped recipe for item %s not found!", item.getName());
        return null;
    }

    public boolean isRecipePresent(LightItem item) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ShapedRecipe WHERE item = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, item.getName());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        boolean exists = rs.getInt(1) > 0;
        rs.close();
        pstmt.close();
        return exists;
    }
}
