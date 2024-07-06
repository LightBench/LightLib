package com.frahhs.lightlib.database;

import com.frahhs.lightlib.LightPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Class for managing the connection to the Light database.
 */
public class DatabaseManager {
    private final String sqlite_path;
    private final String mysql_address;
    private final String mysql_port;
    private final String mysql_username;
    private final String mysql_password;
    private Connection dbConnection = null;

    /**
     * Enum representing the types of supported databases.
     */
    public enum DBType {
        SQLITE,
        MYSQL
    }

    /**
     * Constructor for DatabaseManager.
     *
     * @param plugin The main JavaPlugin instance.
     */
    public DatabaseManager(LightPlugin plugin, String db_name, String mysql_address, String  mysql_port, String  mysql_username, String  mysql_password, String  db_type) {
        this.sqlite_path = plugin.getDataFolder().getAbsolutePath() + "/data/" + db_name + ".db";;
        this.mysql_address = mysql_address;
        this.mysql_port = mysql_port;
        this.mysql_username = mysql_username;
        this.mysql_password = mysql_password;

        // Setup connection
        if (Objects.equals(db_type, "SQLite")) {
            // Create data folder if not exist
            File data_folder = new File(plugin.getDataFolder().getAbsolutePath() + "/data");
            if (!data_folder.exists())
                if (data_folder.mkdir())
                    LightPlugin.getLightLogger().fine("Database folder created!");

            // Create SQLite connection
            createConnection(DBType.SQLITE);
        } else if (Objects.equals(db_type, "MySQL")) {
            // Create MySQL connection
            createConnection(DBType.MYSQL);
        } else {
            LightPlugin.getLightLogger().error("Database type %s selected in the config is not valid, you must choose SQLite or MySQL", db_type);
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
        LightPlugin.getLightLogger().fine("Database info: %s, %s, %s, %s", db_type, db_name, mysql_address, mysql_port);

        // Setup tables
        blocksPlacedTable();
        recipeTable();
    }

    /**
     * Retrieves the current database connection.
     *
     * @return The current database connection.
     */
    public Connection getConnection() {
        return dbConnection;
    }

    /**
     * Creates a connection to the specified database type.
     *
     * @param databaseType The type of database (SQLite or MySQL).
     */
    private void createConnection(DBType databaseType) {
        LightPlugin.getLightLogger().fine("Creating database connection...");
        try {
            if (databaseType == DBType.SQLITE) {
                // Connect to SQLite database
                Class.forName("org.sqlite.JDBC");
                dbConnection = DriverManager.getConnection("jdbc:sqlite:" + sqlite_path);
                dbConnection.setAutoCommit(false);
            } else if (databaseType == DBType.MYSQL) {
                // Connect to MySQL database
                Class.forName("com.mysql.jdbc.Driver");
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + mysql_address + ":" + mysql_port, mysql_username, mysql_password);
                dbConnection.setAutoCommit(false);
            }
            LightPlugin.getLightLogger().fine("Database connection created!");
        } catch (ClassNotFoundException | SQLException e) {
            LightPlugin.getLightLogger().error("Error while creating the database connection.\n%s", e);
        }
    }

    /**
     * Disables the current database connection.
     */
    public void disable() {
        if(dbConnection == null)
            return;

        try {
            if(dbConnection.isClosed())
                return;

            LightPlugin.getLightLogger().fine("Closing database connection...");
            dbConnection.close();
            LightPlugin.getLightLogger().fine("Closed database connection!");
        } catch (SQLException e) {
            LightPlugin.getLightLogger().error(e.toString());
        }
    }

    /**
     * Creates the block placed table if it does not exist.
     */
    public void blocksPlacedTable() {
        LightPlugin.getLightLogger().fine("Creating BlocksPlaced table...");
        Statement stmt;

        // if table safes not exist create it
        try {
            stmt = dbConnection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS BlocksPlaced (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT,"     +
                         "timestamp DEFAULT CURRENT_TIMESTAMP,"      +
                         "placer CHAR(100),"                         +
                         "material CHAR(100) NOT NULL,"              +
                         "entityUUID CHAR(100) NOT NULL,"            +
                         "world CHAR(100) NOT NULL,"                 +
                         "blockX int NOT NULL,"                      +
                         "blockY int NOT NULL,"                      +
                         "blockZ int NOT NULL)"                      ;
            stmt.executeUpdate(sql);
            dbConnection.commit();
            stmt.close();
            LightPlugin.getLightLogger().fine("Created BlocksPlaced table!");
        } catch ( Exception e ) {
            LightPlugin.getLightLogger().error("Error while creating BlocksPlaced table, %s", e);
        }
    }

    /**
     * Creates the recipes table if it does not exist.
     */
    public void recipeTable() {
        LightPlugin.getLightLogger().fine("Creating ShapedRecipe table...");
        Statement stmt;

        // if table safes not exist create it
        try {
            stmt = dbConnection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS ShapedRecipe (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT,"     +
                         "item TEXT UNIQUE,"                         +
                         "pattern TEXT,"                             +
                         "ingredients TEXT)"                         ;

            stmt.executeUpdate(sql);
            dbConnection.commit();
            stmt.close();
            LightPlugin.getLightLogger().fine("Created ShapedRecipe table");
        } catch ( Exception e ) {
            LightPlugin.getLightLogger().error("Error while creating ShapedRecipe table, %s", e);
        }
    }
}
