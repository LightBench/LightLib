package com.frahhs.lightlib;

import com.frahhs.lightlib.util.bag.BagManager;

import java.sql.Connection;

public class LightProvider extends LightObject{
    /** The database connection. */
    protected final Connection dbConnection = plugin.getLightDatabase().getConnection();

    /** The bag manager for managing data bags. */
    protected final BagManager bagManager = plugin.getBagManager();
}
