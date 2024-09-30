package com.frahhs.lightlib.block;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.LightProvider;
import com.frahhs.lightlib.item.ItemManager;
import com.frahhs.lightlib.item.LightItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a block used in the Light feature.
 */
public class LightBlock extends LightProvider {
    private final LightItem item;
    private ItemDisplay itemDisplay;
    private Location location;
    private final Player placer;

    /**
     * Constructs a new LightBlock.
     *
     * @param item The Light item associated with this block.
     * @param location The location of the block.
     */
    public LightBlock(LightItem item, Location location, Player placer) {
        this.item = item;
        this.location = location;
        this.placer = placer;
    }

    /**
     * Sets the location of the block.
     *
     * @param location The new location of the block.
     */
    protected void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Retrieves the item display entity associated with this block.
     *
     * @return ItemDisplay entity.
     */
    protected ItemDisplay getItemDisplay() {
        return itemDisplay;
    }

    /**
     * Sets the item display entity associated with this block.
     *
     * @param itemDisplay The ItemDisplay.
     */
    protected void setItemDisplay(ItemDisplay itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    /**
     * Retrieves the light material of this block.
     *
     * @return The light material.
     */
    public LightItem getItem() {
        return item;
    }

    public Material getMaterial() {
        return item.getMaterial();
    }

    public PersistentDataContainer getPersistentDataContainer() {
        if(itemDisplay == null) {
            LightPlugin.getLightLogger().error("Item Display (safe skin) not found, probably you issued killall command losing all the active safes.");
            throw new RuntimeException("Item Display not found.");
        }

        return itemDisplay.getPersistentDataContainer();
    }

    public UUID getUniqueId() {
        return itemDisplay.getUniqueId();
    }

    public Player getPlacer() {
        return placer;
    }

    /**
     * Places the Light block at the specified location.
     *
     * @param placer The player who is placing the block.
     */
    public void place(Player placer) {
        // Handle block facing
        double y = placer.getLocation().getYaw();
        float direction = 0;
        if (placer.getFacing().equals(BlockFace.SOUTH)) {
            direction = 0;
        }
        if (placer.getFacing().equals(BlockFace.WEST)) {
            direction = 90;
        }
        if (placer.getFacing().equals(BlockFace.NORTH)) {
            direction = 180;
        }
        if (placer.getFacing().equals(BlockFace.EAST)) {
            direction = 270;
        }

        Location location = this.location.clone();
        location.add(0.5, 0.5, 0.5);
        location.setYaw(direction + 180);

        assert location.getWorld() != null;
        final ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);

        itemDisplay.setItemStack(item.getItemStack());
        itemDisplay.setPersistent(true);
        itemDisplay.setBrightness(new Display.Brightness(15, 15));
        setItemDisplay(itemDisplay);

        save(placer);
    }

    /**
     * Destroys the Light block, removing the Item Display too.
     */
    public void destroy() {
        if(!isLightBlock(location.getBlock()))
            return;

        LightBlock block = getFromLocation(location);

        if(block == null)
            return;

        if(block.getItemDisplay() != null)
            block.getItemDisplay().remove();

        location.getBlock().setType(Material.AIR);

        block.remove();
    }

    /**
     * Retrieves the location of the block.
     *
     * @return The location of the block.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Saves the block's state to the database along with the player's details.
     *
     * @param placer The player who placed the block.
     */
    private void save(Player placer) {
        if (itemDisplay == null) {
            throw new RuntimeException("Tried to save a not placed Light block!");
        }

        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("INSERT INTO BlocksPlaced (placer, material, entityUUID, world, blockX, blockY, blockZ) VALUES (?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, placer.getUniqueId().toString());
            ps.setString(2, item.getIdentifier());
            ps.setString(3, itemDisplay.getUniqueId().toString());
            ps.setString(4, location.getWorld().getName());
            ps.setInt(5, location.getBlockX());
            ps.setInt(6, location.getBlockY());
            ps.setInt(7, location.getBlockZ());
            ps.executeUpdate();
            dbConnection.commit();
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
    }

    /**
     * Removes the block's state from the database.
     */
    private void remove() {
        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("DELETE FROM BlocksPlaced WHERE world = ? AND blockX = ? AND blockY = ? AND blockZ = ?;");
            ps.setString(1, location.getWorld().getName());
            ps.setInt(2, location.getBlockX());
            ps.setInt(3, location.getBlockY());
            ps.setInt(4, location.getBlockZ());
            ps.executeUpdate();
            dbConnection.commit();
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
    }

    /**
     * Checks if a block is a LightBlock.
     *
     * @param block The block to check.
     * @return True if the block is a LightBlock, false otherwise.
     */
    public static boolean isLightBlock(Block block) {
        Connection dbConnection = plugin.getLightDatabase().getConnection();

        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("SELECT * FROM BlocksPlaced WHERE world = ? AND blockX = ? AND blockY = ? AND blockZ = ?;");
            ps.setString(1, block.getLocation().getWorld().getName());
            ps.setInt(2, block.getLocation().getBlockX());
            ps.setInt(3, block.getLocation().getBlockY());
            ps.setInt(4, block.getLocation().getBlockZ());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a location corresponds to a LightBlock.
     *
     * @param location The location to check.
     * @return True if the location corresponds to a LightBlock, false otherwise.
     */
    public static boolean isLightBlock(Location location) {
        Connection dbConnection = plugin.getLightDatabase().getConnection();

        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("SELECT * FROM BlocksPlaced WHERE world = ? AND blockX = ? AND blockY = ? AND blockZ = ?;");
            ps.setString(1, Objects.requireNonNull(location.getWorld()).getName());
            ps.setInt(2, location.getBlockX());
            ps.setInt(3, location.getBlockY());
            ps.setInt(4, location.getBlockZ());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
        return false;
    }

    /**
     * Checks if an item display corresponds to a LightBlock.
     *
     * @param entity The entity to check.
     * @return True if the item display corresponds to a LightBlock, false otherwise.
     */
    public static boolean isLightBlock(Entity entity) {
        Connection dbConnection = plugin.getLightDatabase().getConnection();

        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("SELECT * FROM BlocksPlaced WHERE entityUUID = ?;");
            ps.setString(1, entity.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a LightBlock from a location.
     *
     * @param location The location to retrieve the block from.
     * @return The LightBlock at the specified location, or null if none is found.
     */
    public static LightBlock getFromLocation(Location location) {
        Connection dbConnection = plugin.getLightDatabase().getConnection();

        if (!isLightBlock(location)) {
            return null;
        }

        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("SELECT * FROM BlocksPlaced WHERE world = ? AND blockX = ? AND blockY = ? AND blockZ = ?;");
            ps.setString(1, Objects.requireNonNull(location.getWorld()).getName());
            ps.setInt(2, location.getBlockX());
            ps.setInt(3, location.getBlockY());
            ps.setInt(4, location.getBlockZ());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String identifier = rs.getString("material");
                ItemManager itemManager = plugin.getItemsManager();
                LightItem item = itemManager.get(identifier);

                String placer = rs.getString("placer");
                Player player = Bukkit.getPlayer(UUID.fromString(placer));

                LightBlock block = new LightBlock(item, location, player);

                // Entity can be null if someone manually destroyed it
                String entityUUID = rs.getString("entityUUID");
                Entity itemDisplay = Bukkit.getEntity(UUID.fromString(entityUUID));
                if (itemDisplay != null) {
                    if (!itemDisplay.getType().equals(EntityType.ITEM_DISPLAY)) {
                        throw new RuntimeException("Database UUID is not an ItemDisplay UUID");
                    } else {
                        block.setItemDisplay((ItemDisplay) itemDisplay);
                    }
                }

                return block;
            }
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a LightBlock from a UUID.
     *
     * @param entityUUID The UUID of the entity display.
     * @return The LightBlock associated with the entity display, or null if none is found.
     */
    public static LightBlock getFromUUID(UUID entityUUID) {
        Connection dbConnection = plugin.getLightDatabase().getConnection();

        try {
            PreparedStatement ps;
            ps = dbConnection.prepareStatement("SELECT * FROM BlocksPlaced WHERE entityUUID = ?;");
            ps.setString(1, entityUUID.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String identifier = rs.getString("material");
                ItemManager itemManager = plugin.getItemsManager();
                LightItem item = itemManager.get(identifier);

                World world = Bukkit.getWorld(rs.getString("world"));
                int blockX = rs.getInt("blockX");
                int blockY = rs.getInt("blockY");
                int blockZ = rs.getInt("blockZ");

                String placer = rs.getString("placer");
                Player player = Bukkit.getPlayer(UUID.fromString(placer));

                Location location = new Location(world, blockX, blockY, blockZ);
                LightBlock block = new LightBlock(item, location, player);

                // Entity can be null if someone manually destroyed it
                String itemDisplayUUID = rs.getString("entityUUID");
                Entity itemDisplay = Bukkit.getEntity(UUID.fromString(itemDisplayUUID));
                if (itemDisplay != null) {
                    if (!itemDisplay.getType().equals(EntityType.ITEM_DISPLAY)) {
                        throw new RuntimeException("Database UUID is not an item display UUID");
                    } else {
                        block.setItemDisplay((ItemDisplay) itemDisplay);
                    }
                }

                return block;
            }
            ps.close();
        } catch (Exception e) {
            LightPlugin.getLightLogger().error("%s: %s", e.getClass().getName(), e.getMessage());
        }
        return null;
    }
}
