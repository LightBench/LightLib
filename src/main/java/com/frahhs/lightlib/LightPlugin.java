package com.frahhs.lightlib;

import co.aikar.commands.PaperCommandManager;
import com.frahhs.lightlib.block.LightBlockListener;
import com.frahhs.lightlib.database.DatabaseManager;
import com.frahhs.lightlib.feature.FeatureManager;
import com.frahhs.lightlib.gui.GUIListener;
import com.frahhs.lightlib.item.ItemManager;
import com.frahhs.lightlib.provider.Localization;
import com.frahhs.lightlib.util.ConfigManager;
import com.frahhs.lightlib.util.bag.BagManager;
import com.frahhs.lightlib.util.logging.LightLogger;
import com.frahhs.lightlib.util.update.UpdateChecker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

public abstract class LightPlugin extends JavaPlugin {
    private static LightPlugin instance;
    private static Locale locale;

    private static LightLogger logger;

    // Providers
    private static YamlFile config;
    private static Localization Localization;

    // Managers
    private static DatabaseManager databaseManager;
    private static BagManager bagManager;
    private static ItemManager itemManager;
    private static FeatureManager featureManager;
    private static PaperCommandManager commandManager;

    // Options
    private static LightOptions options;

    public void onLightLoad() {}
    public void onLightEnabled() {}
    public void onLightDisabled() {}
    public void onLightReload() {}

    @Override
    public void onLoad() {
        options = new LightOptions();

        try {
            config = new YamlFile(getDataFolder() + File.separator + "config.yml");
            config.createOrLoadWithComments();
            ConfigManager.initConfig(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        onLightLoad();
    }

    @Override
    public void onEnable() {
        instance = this;

        // Set client locale
        Locale.setDefault(Locale.ENGLISH);
        locale = Locale.of(config.getString("language"));

        // Enable logger
        logger = new LightLogger(this.getName(), this);
        logger.setLevel(Level.INFO);

        // Enable managers
        Localization = new Localization(this);
        itemManager = new ItemManager(this);
        commandManager  = new PaperCommandManager(this);
        bagManager = new BagManager();
        featureManager = new FeatureManager(this);

        // Enable Database connection
        databaseManager = new DatabaseManager(
                this,
                config.getString("database.database-name"),
                config.getString("database.mysql.address"),
                config.getString("database.mysql.port"),
                config.getString("database.mysql.username"),
                config.getString("database.mysql.password"),
                config.getString("database.type")
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
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Disable plugin if is disabled in the config
        if(!config.getBoolean("enabled"))
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

    public void onReload() {
        // Config and messages providers
        try {
            config.createOrLoadWithComments();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Localization.reload();

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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale newLocale) {
        locale = newLocale;
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
     * LightLib use SimpleYaml to handle yaml files, for this reason we avoid to use getConfig.
     * Use getLightConfig instead to handle the configuration file.
     *
     * @return the FileConfiguration of the plugin
     */
    @Deprecated
    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    /**
     * Will retrieve the YamlFile of LightConfig, check the repo of SimpleYaml for more info.
     *
     * @return the YamlFile of the config.
     */
    public YamlFile getLightConfig() {
        return config;
    }

    /**
     * Will retrieve the Localization
     *
     * @return the Localization
     */
    public Localization getMessagesProvider() {
        return Localization;
    }

    /**
     * Will retrieve the DatabaseManager
     *
     * @return the DatabaseManager
     */
    public DatabaseManager getLightDatabase() {
        return databaseManager;
    }

    /**
     * Will retrieve the BagManager
     *
     * @return the BagManager
     */
    public BagManager getBagManager() {
        return bagManager;
    }

    /**
     * Will retrieve the ItemManager
     *
     * @return the ItemManager
     */
    public ItemManager getItemsManager() {
        return itemManager;
    }

    /**
     * Will retrieve the FeatureManager
     *
     * @return the FeatureManager
     */
    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    /**
     * Will retrieve the PaperCommandManager
     *
     * @return the PaperCommandManager
     */
    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Will retrieve the LightOptions
     *
     * @return the LightOptions
     */
    public LightOptions getOptions() {
        return options;
    }
}
