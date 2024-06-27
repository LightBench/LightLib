package com.frahhs.lightlib.gui.event;

import com.frahhs.lightlib.LightEvent;
import com.frahhs.lightlib.gui.GUI;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event where a player catches another player.
 */
public class GUIClickEvent extends LightEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    protected final InventoryClickEvent inventoryClickEvent;
    protected final GUI gui;

    /**
     * Constructs a new CatchEvent.
     *
     * @param inventoryClickEvent The InventoryClickEvent instance.
     */
    public GUIClickEvent(@NotNull final GUI gui, @NotNull final InventoryClickEvent inventoryClickEvent) {
        this.inventoryClickEvent = inventoryClickEvent;
        this.gui = gui;
        this.cancel = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Retrieves the InventoryClickEvent.
     *
     * @return The InventoryClickEvent instance.
     */
    public InventoryClickEvent getInventoryClickEvent() {
        return this.inventoryClickEvent;
    }

    public GUI getGui() {
        return gui;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the static list of handlers for the event.
     *
     * @return The static list of handlers.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
