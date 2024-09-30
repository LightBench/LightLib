package com.frahhs.lightlib.util;

import com.frahhs.lightlib.LightPlugin;
import org.simpleyaml.configuration.comments.format.YamlCommentFormat;

import java.io.IOException;

public class ConfigManager {
    public static void initConfig(LightPlugin plugin) throws IOException {
        plugin.getLightConfig().setCommentFormat(YamlCommentFormat.PRETTY);

        generalConfig(plugin);
        generateHeader(plugin);
    }

    private static void generalConfig(LightPlugin plugin) {
        // Enabled
        plugin.getLightConfig().path("enabled")
                .addDefault(true)
                .comment(getDescription(plugin) + ConfigManager.generateHeader(plugin.getName(), true) + "\n\n# Enable or disable the plugin.");

        // Update Check
        plugin.getLightConfig().path("update-check")
                .addDefault(true)
                .comment("""
                        Automatically check for updates.
                        If an new update is released a notification will be sent.""");

        // Language
        plugin.getLightConfig().path("language")
                .addDefault("en")
                .comment("Set the language of the plugin.");

        // Prefix
        plugin.getLightConfig().path("prefix")
                .addDefault(String.format("§3[§6%s§3] §f", plugin.getName()))
                .comment("Prefix for plugin messages.");

        // Database
        plugin.getLightConfig().path("database").path("type")
                .addDefault("SQLite")
                .comment("Type of database you chose to use.\n" +
                         "Supported tags are \"SQLite\" and \"MySQL\" only.\n" +
                         "NOTE: If you select \"MySQL\", you should have your own hosted database.");

        plugin.getLightConfig().path("database").path("database-name")
                .addDefault("LightDB")
                .comment("Database name, you can leave this as default except in particular cases.");

        plugin.getLightConfig().path("database").path("mysql")
                .comment("MySQL access data, ignored if MySQL is not selected.")
                    .addDefault("address", "localhost")
                    .addDefault("port", "3306")
                    .addDefault("username", "")
                    .addDefault("password", "");

        plugin.getLightConfig().path("database").comment("Database settings");
    }

    private static void generateHeader(LightPlugin plugin) {
        plugin.getLightConfig().options().headerFormatter()
                .prefixFirst(
                        "############################################################\n" +
                                "# +------------------------------------------------------+ #")
                .commentPrefix("#")
                .commentSuffix("#")
                .suffixLast(
                        "# +------------------------------------------------------+ #\n" +
                                "############################################################"
                );

        plugin.getLightConfig().setHeader(ConfigManager.generateHeaderMiddle("Notes", false));
    }

    public static String generateHeader(String header, boolean prefix) {
        if(header.length() > 54)
            throw new RuntimeException(String.format("Your header is long %d, the maximum header length is 54", header.length()));

        String prefixFirst1 = "###########################################################\n";
        String prefixFirst2 = " +------------------------------------------------------+ #"  ;
        String suffixLast1 =  " +------------------------------------------------------+ #\n";
        String suffixLast2 =  "###########################################################"  ;

        StringBuilder headerPrefix = new StringBuilder(" |");
        StringBuilder headerSuffix = new StringBuilder("| #");

        if(prefix) {
            prefixFirst1 = "#" + prefixFirst1;
            prefixFirst2 = "#" + prefixFirst2;
            suffixLast1 = "#" + suffixLast1;
            suffixLast2 = "#" + suffixLast2;
            headerPrefix.insert(0, "#");
        }
        String prefixFirst = prefixFirst1 + prefixFirst2;
        String suffixLast = suffixLast1 + suffixLast2;



        int prefixSpacing = (54 - header.length()) / 2;
        int suffixSpacing = (54 - header.length()) - prefixSpacing;

        headerPrefix.append(" ".repeat(prefixSpacing));
        headerSuffix.insert(0, " ".repeat(suffixSpacing));

        return String.format("%s\n%s%s%s\n%s", prefixFirst, headerPrefix, header, headerSuffix, suffixLast);
    }

    public static String generateHeaderMiddle(String header, boolean prefix) {
        if(header.length() > 54)
            throw new RuntimeException(String.format("Your header is long %d, the maximum header length is 54", header.length()));

        StringBuilder headerPrefix = new StringBuilder(" |");
        StringBuilder headerSuffix = new StringBuilder("| ");

        if(prefix) {
            headerPrefix.insert(0, "#");
        }

        int prefixSpacing = (54 - header.length()) / 2;
        int suffixSpacing = (54 - header.length()) - prefixSpacing;

        headerPrefix.append(" ".repeat(prefixSpacing));
        headerSuffix.insert(0, " ".repeat(suffixSpacing));

        return String.format("%s%s%s", headerPrefix, header, headerSuffix);
    }

    private static String getDescription(LightPlugin plugin) {
        return String.format("# This is the config file for %s, developed by %s.\n", plugin.getDescription().getName(), plugin.getDescription().getAuthors()) +
               String.format("# This config was generated for version %s.\n", plugin.getDescription().getVersion()) +
               "\n" +
               "# If you want to use special characters in this document, such as accented letters, you MUST save the file as UTF-8, not ANSI.\n" +
               String.format("# If you receive an error when %s loads, ensure that:\n", plugin.getDescription().getName()) +
               "#   - No tabs are present: YAML only allows spaces.\n" +
               "#   - Indents are correct: YAML hierarchy is based entirely on indentation.\n" +
               "#   - You have \"escaped\" all apostrophes in your text: If you want to write \"don't\", for example, write \"don''t\" instead (note the doubled apostrophe).\n" +
               "#   - Text with symbols is enclosed in single or double quotation marks.\n" +
               "\n" +
               "# Note that if you disable a feature add the inherit sub-feature will be disabled in cascade.\n" +
               "\n" +
               "# If you need help, you can join the community Discord: https://discord.gg/Hh9zMQnWvW.\n" +
               "# Before asking for help, make sure that you tried to recreate the config file (ensure to take a backup of the old one before).\n\n";
    }
}
