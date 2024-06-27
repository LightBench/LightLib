package com.frahhs.lightlib;

import com.frahhs.lightlib.provider.ConfigProvider;
import com.frahhs.lightlib.provider.MessagesProvider;
import com.frahhs.lightlib.util.logging.LightLogger;

public class LightObject {
    protected final LightPlugin plugin = LightPlugin.getInstance();

    /** Configuration manager instance. */
    protected final ConfigProvider config = plugin.getConfigProvider();

    /** Messages manager instance. */
    protected final MessagesProvider messages = plugin.getMessagesProvider();

    /** Logger instance. */
    protected final LightLogger logger = LightPlugin.getLightLogger();
}
