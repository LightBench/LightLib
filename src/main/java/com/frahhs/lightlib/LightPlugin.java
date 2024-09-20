package com.frahhs.lightlib;

import co.aikar.commands.PaperCommandManager;
import com.frahhs.lightlib.block.LightBlockListener;
import com.frahhs.lightlib.config.LightConfig;
import com.frahhs.lightlib.database.DatabaseManager;
import com.frahhs.lightlib.feature.FeatureManager;
import com.frahhs.lightlib.gui.GUIListener;
import com.frahhs.lightlib.item.ItemManager;
import com.frahhs.lightlib.provider.Localization;
import com.frahhs.lightlib.util.bag.BagManager;
import com.frahhs.lightlib.util.logging.LightLogger;
import com.frahhs.lightlib.util.update.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.comments.format.YamlCommentFormatter;
import org.simpleyaml.configuration.file.YamlFile;
import org.yaml.snakeyaml.Yaml;

import java.util.Locale;
import java.util.logging.Level;

public abstract class LightPlugin extends JavaPlugin {
    private static LightPlugin instance;
    private static Locale locale;

    private static LightLogger logger;

    // Providers
    private static LightConfig lightConfig;
    private static Localization Localization;

    // Managers
    private static DatabaseManager databaseManager;
    private static BagManager bagManager;
    private static ItemManager itemManager;
    private static FeatureManager featureManager;
    private static PaperCommandManager commandManager;

    // Options
    private static LightOptions options;

    public void onLightConfiguration() {}
    public void onLightLoad() {}
    public void onLightReload() {};

    public abstract void onLightEnabled();
    public abstract void onLightDisabled();

    @Override
    public void onLoad() {
        options = new LightOptions();

        lightConfig = new LightConfig(this);
        lightConfig.read();

        lightConfig.addBoolean("enabled", true);
        lightConfig.addBoolean("update-check", true);
        lightConfig.addString("language", "en");
        lightConfig.addString("prefix", "§3[§6Robbing§3] §f");
        lightConfig.addString("database.type", "SQLite");
        lightConfig.addString("database.database-name", "LightDB");
        lightConfig.addString("database.mysql.address", "localhost");
        lightConfig.addString("database.mysql.port", "3306");
        lightConfig.addString("database.mysql.username", "");
        lightConfig.addString("database.mysql.password", "");

        onLightConfiguration();

        onLightLoad();
    }

    @Override
    public void onEnable() {
        instance = this;

        // Set client locale
        Locale.setDefault(Locale.ENGLISH);
        locale = Locale.of(lightConfig.getConfig().getString("language"));

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
                lightConfig.getConfig().getString("database.database-name"),
                lightConfig.getConfig().getString("database.mysql.address"),
                lightConfig.getConfig().getString("database.mysql.port"),
                lightConfig.getConfig().getString("database.mysql.username"),
                lightConfig.getConfig().getString("database.mysql.password"),
                lightConfig.getConfig().getString("database.type")
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
        lightConfig.save();

        // Disable plugin if is disabled in the config
        if(!lightConfig.getConfig().getBoolean("enabled"))
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
        lightConfig.read();
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
     * Will retrieve the LightConfig
     *
     * @return the LightConfig
     */
    public LightConfig getLightConfig() {
        return lightConfig;
    }

    /**
     * Will retrieve the LightConfig
     *
     * @return the LightConfig
     */
    public YamlFile getYamlConfig() {
        return lightConfig.getConfig();
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
