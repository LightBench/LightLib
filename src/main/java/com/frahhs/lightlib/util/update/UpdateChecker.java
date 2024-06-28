package com.frahhs.lightlib.util.update;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.provider.ConfigProvider;
import com.frahhs.lightlib.util.logging.ConsoleColor;
import com.frahhs.lightlib.util.logging.LightLogger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {
    private final String url = "https://api.spigotmc.org/legacy/update.php?resource=";
    private static String id;

    private static boolean isAvailable = false;

    /**
     * Check if a new update is out on spigot
     *
     * @param id the spigot resource id.
     */
    public UpdateChecker(String id) {
        this.id = id;
    }

    public void enable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean isAvailable() {
        return UpdateChecker.isAvailable;
    }

    @EventHandler
    public void onAdminJoin(PlayerJoinEvent event) {
        ConfigProvider config = LightPlugin.getConfigProvider();

        // Check if new version is out
        if(config.getBoolean("update-check")) {
            check();
        }

        if(UpdateChecker.isAvailable && config.getBoolean("update-check")) {
            if(event.getPlayer().hasPermission(LightPlugin.getOptions().getPermissionPrefix() + ".admin")) {
                String prefix = LightPlugin.getMessagesProvider().getPrefix();
                String message =
                    prefix + ChatColor.DARK_GREEN + "New version of LightPlugin is out!\n" +
                    prefix + ChatColor.DARK_GREEN + "New version is: " + ChatColor.GOLD + getNewVersion() + ChatColor.DARK_GREEN + ".\n" +
                    prefix + ChatColor.DARK_GREEN + "Your actual version is: " + ChatColor.RED + LightPlugin.getInstance().getDescription().getVersion() + ChatColor.DARK_GREEN + ".\n" +
                    prefix + ChatColor.DARK_GREEN + "Download at: \n" +
                    prefix + ChatColor.DARK_GREEN + "https://www.spigotmc.org/resources/LightPlugin.117484/";

                event.getPlayer().sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        ConfigProvider config = LightPlugin.getConfigProvider();

        // Check if new version is out
        if(config.getBoolean("update-check")) {
            check();
        }

        // Check if new version is out
        if(UpdateChecker.isAvailable && config.getBoolean("update-check")) {
            LightLogger logger = LightPlugin.getLightLogger();
            logger.warning("=====================================================");
            logger.warning("New version of LightPlugin is out!");
            logger.warning("New version is: " + ConsoleColor.DARK_GREEN + getNewVersion() + ConsoleColor.YELLOW + ".");
            logger.warning("Your actual version is: " + ConsoleColor.RED + LightPlugin.getInstance().getDescription().getVersion() + ConsoleColor.YELLOW + "." );
            logger.warning("Download at:");
            logger.warning("https://www.spigotmc.org/resources/LightPlugin.117484/");
            logger.warning("=====================================================");
        }
    }

    public String getNewVersion() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url + id).openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            String remoteVersion;
            if(raw.contains("-")) {
                remoteVersion = raw.split("-")[0].trim();
            } else {
                remoteVersion = raw;
            }

            return remoteVersion;

        } catch (IOException e) {
            return "";
        }
    }

    public void check() {
        UpdateChecker.isAvailable = checkUpdate();
    }

    private boolean checkUpdate() {
        try {
            String localVersion = LightPlugin.getInstance().getDescription().getVersion();
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url + id).openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            String remoteVersion;
            if(raw.contains("-")) {
                remoteVersion = raw.split("-")[0].trim();
            } else {
                remoteVersion = raw;
            }

            if(!localVersion.equalsIgnoreCase(remoteVersion))
                return true;

        } catch (IOException e) {
            return false;
        }
        return false;
    }

}