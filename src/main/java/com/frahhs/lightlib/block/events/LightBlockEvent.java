package com.frahhs.lightlib.block.events;

import com.frahhs.lightlib.LightEvent;
import com.frahhs.lightlib.block.LightBlock;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class LightBlockEvent extends LightEvent {
    private static final HandlerList handlers = new HandlerList();
    protected LightBlock block;

    public LightBlockEvent(@NotNull final LightBlock theBlock) {
        block = theBlock;
    }

    /**
     * Gets the Light block involved in this event.
     *
     * @return The Block which block is involved in this event
     */
    @NotNull
    public final LightBlock getBlock() {
        return block;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
