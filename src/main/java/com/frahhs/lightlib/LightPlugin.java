package com.frahhs.lightlib;

import co.aikar.commands.PaperCommandManager;
import com.frahhs.lightlib.block.LightBlockListener;
import com.frahhs.lightlib.database.DatabaseManager;
import com.frahhs.lightlib.feature.FeatureManager;
import com.frahhs.lightlib.gui.GUIListener;
import com.frahhs.lightlib.item.ItemManager;
import com.frahhs.lightlib.provider.ConfigProvider;
import com.frahhs.lightlib.provider.MessagesProvider;
import com.frahhs.lightlib.util.bag.BagManager;
import com.frahhs.lightlib.util.logging.LightLogger;
import com.frahhs.lightlib.util.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class LightPlugin extends JavaPlugin {
    private static LightPlugin instance;

    private static LightLogger logger;

    // Providers
    private static ConfigProvider configProvider;
    private static MessagesProvider messagesProvider;

    // Managers
    private static DatabaseManager databaseManager;
    private static BagManager bagManager;
    private static ItemManager itemManager;
    private static FeatureManager featureManager;
    private static PaperCommandManager commandManager;

    // Options
    private static LightOptions options;

    public void onLightLoad() {}
    public void onLightReload() {};

    public abstract void onLightEnabled();
    public abstract void onLightDisabled();

    @Override
    public void onEnable() {
        instance = this;

        configProvider = new ConfigProvider(this);

        // Enable logger
        logger = new LightLogger(this.getName(), this);
        logger.setLevel(Level.INFO);

        // Enable managers
        messagesProvider = new MessagesProvider(this);

        itemManager = new ItemManager(this);
        commandManager  = new PaperCommandManager(this);
        bagManager = new BagManager();
        featureManager = new FeatureManager(this);

        // Enable Database connection
        databaseManager = new DatabaseManager(
                this,
                configProvider.getString("database.database-name"),
                configProvider.getString("database.mysql.address"),
                configProvider.getString("database.mysql.port"),
                configProvider.getString("database.mysql.username"),
                configProvider.getString("database.mysql.password"),
                configProvider.getString("database.type")
        );

        getServer().getPluginManager().registerEvents(new LightBlockListener(),this);
        getServer().getPluginManager().registerEvents(new GUIListener(),this);

        if(options.getUpdateCheck()) {
            if(options.getSpigotMarketID() == null) {
                logger.warning("Update checker is on, but the Spigot Market ID is null.");
            } else {
                UpdateChecker updateChecker = new UpdateChecker(options.getSpigotMarketID());
                updateChecker.enable(this);
            }
        }

        onLightEnabled();

        // Disable plugin if is disabled in the config
        if(!configProvider.getBoolean("enabled"))
            this.getPluginLoader().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        // Disable features
        if(featureManager != null)
            featureManager.disableFeatures();

        // Dispose items
        if(itemManager != null)
            itemManager.dispose();

        // Disable database
        if(databaseManager != null)
            databaseManager.disable();

        // Disable bags
        if(bagManager != null)
            bagManager.disableBags();

        // Close logger
        if(logger != null)
            logger.close();

        onLightDisabled();
    }

    @Override
    public void onLoad() {
        options = new LightOptions();
        onLightLoad();
    }

    public void onReload() {
        // Config and messages providers
        configProvider.reload();
        messagesProvider.reload();

        // Item
        itemManager.dispose();

        // Bag
        bagManager.disableBags();
        bagManager.enableBags();

        // Feature
        featureManager.disableFeatures();
        featureManager.enableFeatures();

        onLightReload();
    }

    public static LightPlugin getInstance() {
        return instance;
    }

    /**
     * Will retrieve the LightLogger
     *
     * @return the LightLogger
     */
    public static LightLogger getLightLogger() {
        return logger;
    }

    /**
     * Will retrieve the ConfigProvider
     *
     * @return the ConfigProvider
     */
    public static ConfigProvider getConfigProvider() {
        return configProvider;
    }

    /**
     * Will retrieve the MessagesProvider
     *
     * @return the MessagesProvider
     */
    public static MessagesProvider getMessagesProvider() {
        return messagesProvider;
    }

    /**
     * Will retrieve the DatabaseManager
     *
     * @return the DatabaseManager
     */
    public static DatabaseManager getLightDatabase() {
        return databaseManager;
    }

    /**
     * Will retrieve the BagManager
     *
     * @return the BagManager
     */
    public static BagManager getBagManager() {
        return bagManager;
    }

    /**
     * Will retrieve the ItemManager
     *
     * @return the ItemManager
     */
    public static ItemManager getItemsManager() {
        return itemManager;
    }

    /**
     * Will retrieve the FeatureManager
     *
     * @return the FeatureManager
     */
    public static FeatureManager getFeatureManager() {
        return featureManager;
    }

    /**
     * Will retrieve the PaperCommandManager
     *
     * @return the PaperCommandManager
     */
    public static PaperCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Will retrieve the LightOptions
     *
     * @return the LightOptions
     */
    public static LightOptions getOptions() {
        return options;
    }
}
