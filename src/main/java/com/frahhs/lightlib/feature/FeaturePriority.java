package com.frahhs.lightlib.feature;

public enum FeaturePriority {
    LOWEST(1),
    LOW(2),
    NORMAL(3),
    HIGH(4),
    HIGHEST(5),
    MONITOR(6);

    private final int slot;

    private FeaturePriority(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}


