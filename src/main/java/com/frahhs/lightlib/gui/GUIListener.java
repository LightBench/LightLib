package com.frahhs.lightlib.gui;

import com.frahhs.lightlib.LightListener;
import com.frahhs.lightlib.gui.event.GUIClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener extends LightListener {

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null)
            return;

        if(! (e.getClickedInventory().getHolder() instanceof GUI))
            return;

        e.setCancelled(true);

        GUI gui = (GUI) e.getInventory().getHolder();
        assert gui != null;

        // Call StartStealEvent
        GUIClickEvent startLightEvent = new GUIClickEvent(gui, e);
        Bukkit.getPluginManager().callEvent(startLightEvent);
    }
}
