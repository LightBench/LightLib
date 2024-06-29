package com.frahhs.lightlib.block;

import com.frahhs.lightlib.LightListener;
import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.block.events.LightBlockBreakEvent;
import com.frahhs.lightlib.block.events.LightBlockInteractEvent;
import com.frahhs.lightlib.block.events.LightBlockPlaceEvent;
import com.frahhs.lightlib.item.ItemManager;
import com.frahhs.lightlib.item.LightItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LightBlockListener extends LightListener {
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemManager itemManager = LightPlugin.getItemsManager();
        ItemStack item = e.getItemInHand();

        // Check if is a Light item.
        if(!itemManager.isRegistered(item))
            return;

        // Instance of the Light item.
        LightItem lightItem = itemManager.get(item);

        // Check if is a Light block.
        if(!lightItem.getVanillaMaterial().isBlock())
            return;

        // Instance of the Light block.
        LightBlock block = new LightBlock(lightItem, e.getBlock().getLocation().clone());

        // Do the place action.
        block.place(e.getPlayer());

        // LightBlockPlaceEvent event.
        LightBlockPlaceEvent lightBlockPlaceEvent = new LightBlockPlaceEvent(block, e);

        // Can build option.
        lightBlockPlaceEvent.setBuild(e.canBuild());

        // call LightBlockPlaceEvent event.
        Bukkit.getPluginManager().callEvent(lightBlockPlaceEvent);

        if(lightBlockPlaceEvent.isCancelled()) {
            block.destroy();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        ItemManager itemManager = LightPlugin.getItemsManager();

        // Check if is a Light item
        if(!LightBlock.isLightBlock(e.getBlock().getLocation()))
            return;

        // Instance of the Light block
        LightBlock block = LightBlock.getFromLocation(e.getBlock().getLocation());

        if(block == null)
            return;

        // Instance of the Light item
        LightItem item = itemManager.get(block.getItem().getClass());

        // Call the LightBlockBreakEvent event
        LightBlockBreakEvent lightBlockBreakEvent = new LightBlockBreakEvent(block, e);
        Bukkit.getPluginManager().callEvent(lightBlockBreakEvent);

        // If the LightBlockPlaceEvent event is cancelled, cancel the action
        if(lightBlockBreakEvent.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        // Drop option
        if(lightBlockBreakEvent.isDropItems() && e.isDropItems() && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), item.getItemStack());
        }

        e.setDropItems(false);

        // Exp option
        e.setExpToDrop(lightBlockBreakEvent.getExpToDrop());

        block.destroy();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null)
            return;

        if(!LightBlock.isLightBlock(e.getClickedBlock())) {
            return;
        }

        // Call the LightBlockBreakEvent event
        LightBlock block = LightBlock.getFromLocation(e.getClickedBlock().getLocation());
        assert block != null;
        LightBlockInteractEvent lightBlockInteractEvent = new LightBlockInteractEvent(block, e);
        Bukkit.getPluginManager().callEvent(lightBlockInteractEvent);

        // If the LightBlockPlaceEvent event is cancelled, cancel the action
        if(lightBlockInteractEvent.isCancelled()) {
            e.setCancelled(true);
        }
    }
}
