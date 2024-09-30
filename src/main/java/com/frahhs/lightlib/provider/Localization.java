package com.frahhs.lightlib.provider;

import com.frahhs.lightlib.LightPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Manages localization for a Spigot plugin using YAML configuration files.
 */
public class Localization {
    private final LightPlugin plugin;
    private String prefix;
    private Map<Locale, YamlFile> lang;

    /**
     * Constructs a new MessagesManager.
     *
     * @param plugin The LightPlugin instance owning this manager.
     */
    public Localization(LightPlugin plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getLightConfig().getString("prefix");
        this.lang = new HashMap<>();

        loadLanguageFiles();
    }

    /**
     * Reloads the Localization, updating language settings and reloading language files.
     */
    public void reload() {
        this.prefix = plugin.getLightConfig().getString("prefix");

        loadLanguageFiles();
        if (!lang.containsKey(plugin.getLocale())) {
            LightPlugin.getLightLogger().warning("Language \"%s\" not found! English automatically selected.", plugin.getLocale());
            plugin.setLocale(Locale.ENGLISH);
        }
    }

    /**
     * Retrieves a localized message for the given language and key.
     *
     * @param key The key of the message to retrieve.
     * @param usePrefix If true the prefix will be added at the message head.
     * @return The localized message, or null if not found.
     */
    public String getMessage(String key, boolean usePrefix) {
        YamlFile config = lang.get(plugin.getLocale());
        if (config == null) {
            plugin.getLightLogger().warning("Language files for '%s' not found.", plugin.getLocale());
            return null;
        }

        String message = "";

        if(!config.contains(key)) {
            YamlFile configEng = lang.get(Locale.ENGLISH);
            if(!configEng.contains(key)) {
                plugin.getLightLogger().error("The message path '%s' was not found, empty value used. Try to regen the lang folder.", key);
            } else {
                plugin.getLightLogger().warning("The lang path '%s' does not exist for the language '%s', english used.", key, lang);
                message = configEng.getString(key);
            }
        } else {
            message = config.getString(key);
        }

        assert message != null;
        message = message.replace("&", "§");

        if(usePrefix)
            return String.format("%s%s", prefix, message);
        else
            return message;
    }

    /**
     * Retrieves a localized message for the given language and key.
     *
     * @param key The key of the message to retrieve.
     * @return The localized message, or null if not found.
     */
    public String getMessage(String key) {
        return getMessage(key, true);
    }

    /**
     * Retrieves the plugin prefix.
     *
     * @return The prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Loads language configuration files from the plugin's 'lang' folder.
     */
    private void loadLanguageFiles() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            if(!langDir.mkdirs()) {
                plugin.getLightLogger().error("Error while creating lang folder.");
            }
        }

        plugin.saveResource("lang", false);
        String[] languages = langDir.list((dir, name) -> name.endsWith(".yml"));

        assert languages != null;
        for (String lang : languages) {
            Locale locale = Locale.of(lang.replace(".yml", ""));
            YamlFile langFile = new YamlFile(plugin.getDataFolder() + File.separator + "lang" + File.separator + lang);
            try {
                langFile.load();
            } catch (IOException e) {
                plugin.getLightLogger().warning("Error while loading lang file %s.\n%s", lang, e);
            }
            this.lang.put(locale, langFile);
        }
    }
}
