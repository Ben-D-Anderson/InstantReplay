package com.terraboxstudios.instantreplay.mysql;

import com.terraboxstudios.instantreplay.containers.*;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.InventorySerializer;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MySQL {

	private static Connection connection;

	public MySQL() throws Exception {
		String username = Config.getConfig().getString("mysql.username");
		String password = Config.getConfig().getString("mysql.password");
		String host = Config.getConfig().getString("mysql.host");
		String database = Config.getConfig().getString("mysql.database");
		int port = Config.getConfig().getInt("mysql.port");
		
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" +
				port + "/" + database + "?useSSL=false&autoReconnect=true&maxReconnects=3", username, password);
		initTables();
	}
	
	private void initTables() {
		final String[] tables = {
				"block_events (location VARCHAR(255), old_block VARCHAR(255), old_block_data BLOB, new_block VARCHAR(255), new_block_data BLOB, time BIGINT)",
				"player_move_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), time BIGINT)",
				"death_damage_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), event_type VARCHAR(255), source VARCHAR(255), time BIGINT)",
				"player_inventory_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), serialized MEDIUMTEXT, time BIGINT)",
				"join_leave_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), event_type VARCHAR(255), time BIGINT)"};
		for (String table : tables) {
			try {
				PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + table);
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the mysql connection
	 * @return mysql connection
	 */
	public static Connection getConnection() {
		return connection;
	}
	
	/**
	 * Delete all entries from all replay tables in the database
	 */
	public static void clearLogs() {
		String[] tables = {"block_events", "player_move_events", "death_damage_events", "player_inventory_events", "join_leave_events"};
		for (String table : tables) {
			try {
				PreparedStatement statement = getConnection().prepareStatement("DELETE FROM " + table);
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Log a block event to the mysql database
	 * @param blockEventObj the BlockEventObj of the event
	 */
	public static void logBlockEvent(BlockEventContainer blockEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO block_events (location, old_block, old_block_data, new_block, new_block_data, time) VALUES (?, ?, ?, ?, ?, ?)");
			statement.setString(1, Utils.LocationToString(blockEventObj.getLoc()));
			statement.setString(2, blockEventObj.getOldBlockMaterial().toString());
			statement.setByte(3, blockEventObj.getOldBlockData());
			statement.setString(4, blockEventObj.getNewBlockMaterial().toString());
			statement.setByte(5, blockEventObj.getNewBlockData());
			statement.setLong(6, blockEventObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Log a player move event to the mysql database
	 * @param playerMoveObj the PlayerMoveObj of the event
	 */
	public static void logPlayerMoveEvent(PlayerMoveEventContainer playerMoveObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO player_move_events (name, UUID, location, time) VALUES (?, ?, ?, ?)");
			statement.setString(1, playerMoveObj.getName());
			statement.setString(2, playerMoveObj.getUuid().toString());
			statement.setString(3, Utils.PreciseLocationToString(playerMoveObj.getLocation()));
			statement.setLong(4, playerMoveObj.getTime());
			
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Log a death or damage event to the mysql database
	 * @param deathDamageEventObj the DeathDamageEventObj of the event
	 */
	public static void logDeathDamageEvent(DeathDamageEventContainer deathDamageEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO death_damage_events (name, UUID, location, event_type, source, time) VALUES (?, ?, ?, ?, ?, ?)");
			statement.setString(1, deathDamageEventObj.getName());
			statement.setString(2, deathDamageEventObj.getUuid().toString());
			statement.setString(3, Utils.LocationToString(deathDamageEventObj.getLocation()));
			statement.setString(4, deathDamageEventObj.getType());
			statement.setString(5, deathDamageEventObj.getSource());
			statement.setLong(6, deathDamageEventObj.getTime());
			
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Log a join or leave event to the mysql database
	 * @param joinLeaveEventObj the JoinLeaveEventObj of the event
	 */
	public static void logJoinLeaveEvent(JoinLeaveEventContainer joinLeaveEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO join_leave_events (name, UUID, location, event_type, time) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, joinLeaveEventObj.getName());
			statement.setString(2, joinLeaveEventObj.getUuid().toString());
			statement.setString(3, Utils.LocationToString(joinLeaveEventObj.getLocation()));
			statement.setString(4, joinLeaveEventObj.getType());
			statement.setLong(5, joinLeaveEventObj.getTime());
			
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Log a player inventory event to the mysql database
	 * @param playerInventoryObj the PlayerInventoryObj of the event
	 */
	public static void logPlayerInventoryEvent(PlayerInventoryEventContainer playerInventoryObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO player_inventory_events (name, UUID, location, serialized, time) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, playerInventoryObj.getName());
			statement.setString(2, playerInventoryObj.getUuid().toString());
			statement.setString(3, Utils.LocationToString(playerInventoryObj.getLocation()));
			statement.setString(4, playerInventoryObj.getSerializedInventory());
			statement.setLong(5, playerInventoryObj.getTime());
			
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<BlockEventContainer> getBlockEvents(Location location, int radius, int seconds, long timeStamp) {
		long time;
		if (timeStamp > 1) {
			time = timeStamp;
		} else {
			time = Calendar.getInstance().getTime().getTime() - (seconds * 1000L);
		}
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM block_events WHERE time>=?");
			statement.setLong(1, time);

			ArrayList<BlockEventContainer> blockEvents = new ArrayList<>();

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location loc = Utils.StringToLocation(locString);
				if ((loc.getBlockX() >= location.getBlockX() - radius && loc.getBlockX() <= location.getBlockX() + radius) && (loc.getBlockZ() >= location.getBlockZ() - radius && loc.getBlockZ() <= location.getBlockZ() + radius) && loc.getWorld().getName().equals(location.getWorld().getName())) {
					blockEvents.add(new BlockEventContainer(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), Material.getMaterial(results.getString("old_block")), results.getByte("old_block_data"), Material.getMaterial(results.getString("new_block")), results.getByte("new_block_data"), results.getLong("time")));
				}
			}
			return blockEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	

	public static ArrayList<PlayerMoveEventContainer> getPlayerMoveEvents(Location location, int radius, int seconds, long timeStamp) {
		long time;
		if (timeStamp > 1) {
			time = timeStamp;
		} else {
			time = Calendar.getInstance().getTime().getTime() - (seconds * 1000L);
		}
		radius += 4;
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM player_move_events WHERE time>=?");
			statement.setLong(1, time);

			ArrayList<PlayerMoveEventContainer> playerMoveEvents = new ArrayList<>();

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location loc = Utils.StringToLocation(locString);
				if ((loc.getBlockX() >= location.getBlockX() - radius && loc.getBlockX() <= location.getBlockX() + radius) && (loc.getBlockZ() >= location.getBlockZ() - radius && loc.getBlockZ() <= location.getBlockZ() + radius) && loc.getWorld().getName().equals(location.getWorld().getName())) {
					playerMoveEvents.add(new PlayerMoveEventContainer(results.getString("name"), UUID.fromString(results.getString("UUID")), loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), results.getLong("time")));
				}
			}
			return playerMoveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static ArrayList<DeathDamageEventContainer> getDeathDamageEvents(Location location, int radius, int seconds, long timeStamp) {
		long time;
		if (timeStamp > 1) {
			time = timeStamp;
		} else {
			time = Calendar.getInstance().getTime().getTime() - (seconds * 1000L);
		}
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM death_damage_events WHERE time>=?");
			statement.setLong(1, time);

			ArrayList<DeathDamageEventContainer> deathDamageEvents = new ArrayList<>();

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location loc = Utils.StringToLocation(locString);
				if ((loc.getBlockX() >= location.getBlockX() - radius && loc.getBlockX() <= location.getBlockX() + radius) && (loc.getBlockZ() >= location.getBlockZ() - radius && loc.getBlockZ() <= location.getBlockZ() + radius) && loc.getWorld().getName().equals(location.getWorld().getName())) {
					deathDamageEvents.add(new DeathDamageEventContainer(results.getString("event_type"), results.getString("source"), results.getString("name"), UUID.fromString(results.getString("UUID")), loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), results.getLong("time")));
				}
			}
			return deathDamageEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static ArrayList<JoinLeaveEventContainer> getJoinLeaveEvents(Location location, int radius, int seconds, long timeStamp) {
		long time;
		if (timeStamp > 1) {
			time = timeStamp;
		} else {
			time = Calendar.getInstance().getTime().getTime() - (seconds * 1000L);
		}
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM join_leave_events WHERE time>=?");
			statement.setLong(1, time);

			ArrayList<JoinLeaveEventContainer> joinLeaveEvents = new ArrayList<>();

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location loc = Utils.StringToLocation(locString);
				if ((loc.getBlockX() >= location.getBlockX() - radius && loc.getBlockX() <= location.getBlockX() + radius) && (loc.getBlockZ() >= location.getBlockZ() - radius && loc.getBlockZ() <= location.getBlockZ() + radius) && loc.getWorld().getName().equals(location.getWorld().getName())) {
					joinLeaveEvents.add(new JoinLeaveEventContainer(results.getString("event_type"), results.getString("name"), UUID.fromString(results.getString("UUID")), loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), results.getLong("time")));
				}
			}
			return joinLeaveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static ArrayList<PlayerInventoryEventContainer> getPlayerInventoryEvents(Location location, int radius, int seconds, long timeStamp) {
		long time;
		if (timeStamp > 1) {
			time = timeStamp;
		} else {
			time = Calendar.getInstance().getTime().getTime() - (seconds * 1000L);
		}
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM player_inventory_events WHERE time>=?");
			statement.setLong(1, time);

			ArrayList<PlayerInventoryEventContainer> playerInventoryEvents = new ArrayList<>();

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location loc = Utils.StringToLocation(locString);
				if ((loc.getBlockX() >= location.getBlockX() - radius && loc.getBlockX() <= location.getBlockX() + radius) && (loc.getBlockZ() >= location.getBlockZ() - radius && loc.getBlockZ() <= location.getBlockZ() + radius) && loc.getWorld().getName().equals(location.getWorld().getName())) {
					String[] ser = results.getString("serialized").split(";");
					ItemStack[] content = InventorySerializer.itemStackArrayFromBase64(ser[0]);
					ItemStack[] armour = InventorySerializer.itemStackArrayFromBase64(ser[1]);
					int slot = Integer.parseInt(ser[2]);
					ItemStack[] health = InventorySerializer.itemStackArrayFromBase64(ser[3]);
					playerInventoryEvents.add(new PlayerInventoryEventContainer(results.getString("name"), UUID.fromString(results.getString("UUID")), results.getString("serialized"), content, armour, health, slot, loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), results.getLong("time")));
				}
			}
			return playerInventoryEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}


}
