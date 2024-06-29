package com.frahhs.lightlib;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class LightEvent extends Event {
    @NotNull
    @Override
    public abstract HandlerList getHandlers();
}
