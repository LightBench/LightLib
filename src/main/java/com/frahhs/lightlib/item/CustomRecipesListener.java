package com.frahhs.lightlib.item;

import com.frahhs.lightlib.LightListener;
import com.frahhs.lightlib.LightPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CustomRecipesListener extends LightListener {

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getRecipe().getResult();

        ItemManager itemManager = LightPlugin.getItemsManager();

        for(LightItem cur : itemManager.getRegisteredItems()) {
            // Check if cur custom item is craftable
            if(cur.isCraftable()) {
                // Check if it is a custom item
                if (cur.getItemStack().isSimilar(item)) {
                    // Check if player have permission
                    if (!player.hasPermission(String.format(LightPlugin.getOptions().getPermissionPrefix() + ".craft.%s", cur.getName().toLowerCase()))) {
                        String message = messages.getMessage("general.no_permissions");
                        player.sendMessage(message);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
