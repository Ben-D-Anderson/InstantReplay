package com.terraboxstudios.instantreplay.mysql;

import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.containers.*;
import com.terraboxstudios.instantreplay.inventory.InventorySerializer;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL {

	private Connection connection;

	private static MySQL instance;

	public static MySQL getInstance() {
		if (instance == null) instance = new MySQL();
		return instance;
	}

	private MySQL() {
		String username = Config.getConfig().getString("mysql.username");
		String password = Config.getConfig().getString("mysql.password");
		String host = Config.getConfig().getString("mysql.host");
		String database = Config.getConfig().getString("mysql.database");
		int port = Config.getConfig().getInt("mysql.port");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" +
					port + "/" + database, username, password);
			initTables();
		} catch (ClassNotFoundException | SQLException exception) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to connect to MySQL database!");
			Bukkit.getPluginManager().disablePlugin(Main.getPlugin(Main.class));
		}
	}

	private final String[] tables = {
			"block_events (location VARCHAR(255), old_block VARCHAR(255), old_block_data BLOB, new_block VARCHAR(255), new_block_data BLOB, time BIGINT)",
			"player_move_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), time BIGINT)",
			"death_damage_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), event_type VARCHAR(255), source VARCHAR(255), time BIGINT)",
			"player_inventory_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), serialized MEDIUMTEXT, time BIGINT)",
			"join_leave_events (name VARCHAR(255), UUID VARCHAR(255), location VARCHAR(255), event_type VARCHAR(255), time BIGINT)"
	};

	private void initTables() {
		for (String table : tables) {
			try {
				PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + table);
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized Connection getConnection() {
		return connection;
	}

	/**
	 * Delete all entries from all replay tables in the database
	 */
	public void clearLogs() {
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

	public void logEvent(EventContainer eventContainer) {
		if (eventContainer instanceof BlockEventContainer) {
			logBlockEvent((BlockEventContainer) eventContainer);
		} else if (eventContainer instanceof PlayerMoveEventContainer) {
			logPlayerMoveEvent((PlayerMoveEventContainer) eventContainer);
		} else if (eventContainer instanceof DeathDamageEventContainer) {
			logDeathDamageEvent((DeathDamageEventContainer) eventContainer);
		} else if (eventContainer instanceof JoinLeaveEventContainer) {
			logJoinLeaveEvent((JoinLeaveEventContainer) eventContainer);
		} else if (eventContainer instanceof PlayerInventoryEventContainer) {
			logPlayerInventoryEvent((PlayerInventoryEventContainer) eventContainer);
		} else {
			throw new IllegalArgumentException("Unsupported event container type.");
		}
	}

	private void logBlockEvent(BlockEventContainer blockEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO block_events (location, old_block, old_block_data, new_block, new_block_data, time) VALUES (?, ?, ?, ?, ?, ?)");
			statement.setString(1, Utils.locationToString(blockEventObj.getLocation()));
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

	private void logPlayerMoveEvent(PlayerMoveEventContainer playerMoveObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO player_move_events (name, UUID, location, time) VALUES (?, ?, ?, ?)");
			statement.setString(1, playerMoveObj.getName());
			statement.setString(2, playerMoveObj.getUuid().toString());
			statement.setString(3, Utils.preciseLocationToString(playerMoveObj.getLocation()));
			statement.setLong(4, playerMoveObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logDeathDamageEvent(DeathDamageEventContainer deathDamageEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO death_damage_events (name, UUID, location, event_type, source, time) VALUES (?, ?, ?, ?, ?, ?)");
			statement.setString(1, deathDamageEventObj.getName());
			statement.setString(2, deathDamageEventObj.getUuid().toString());
			statement.setString(3, Utils.locationToString(deathDamageEventObj.getLocation()));
			statement.setString(4, deathDamageEventObj.getType());
			statement.setString(5, deathDamageEventObj.getSource());
			statement.setLong(6, deathDamageEventObj.getTime());
			
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logJoinLeaveEvent(JoinLeaveEventContainer joinLeaveEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO join_leave_events (name, UUID, location, event_type, time) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, joinLeaveEventObj.getName());
			statement.setString(2, joinLeaveEventObj.getUuid().toString());
			statement.setString(3, Utils.locationToString(joinLeaveEventObj.getLocation()));
			statement.setString(4, joinLeaveEventObj.getType());
			statement.setLong(5, joinLeaveEventObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logPlayerInventoryEvent(PlayerInventoryEventContainer playerInventoryObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO player_inventory_events (name, UUID, location, serialized, time) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, playerInventoryObj.getName());
			statement.setString(2, playerInventoryObj.getUuid().toString());
			statement.setString(3, Utils.locationToString(playerInventoryObj.getLocation()));
			statement.setString(4, playerInventoryObj.getSerializedInventory());
			statement.setLong(5, playerInventoryObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BlockEventContainer> getBlockEvents(Location replayLocation, int radius, int seconds, long timeStamp) {
		ArrayList<BlockEventContainer> blockEvents = new ArrayList<>();
		if (replayLocation.getWorld() == null) return blockEvents;

		long time = timeStamp > 1 ? timeStamp : Calendar.getInstance().getTimeInMillis() - (seconds * 1000L);

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM block_events WHERE time>=?");
			statement.setLong(1, time);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);
				eventLocation.setX(eventLocation.getBlockX());
				eventLocation.setY(eventLocation.getBlockY());
				eventLocation.setZ(eventLocation.getBlockZ());

				if (isLocationInReplay(eventLocation, replayLocation, radius)) {
					blockEvents.add(new BlockEventContainer(UUID.randomUUID(), eventLocation, results.getLong("time"), Material.getMaterial(results.getString("old_block")), Material.getMaterial(results.getString("new_block")), results.getByte("old_block_data"), results.getByte("new_block_data")));
				}
			}
			return blockEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blockEvents;
	}


	public ArrayList<PlayerMoveEventContainer> getPlayerMoveEvents(Location replayLocation, int radius, int seconds, long timeStamp) {
		ArrayList<PlayerMoveEventContainer> playerMoveEvents = new ArrayList<>();
		if (replayLocation.getWorld() == null) return playerMoveEvents;

		long time = timeStamp > 1 ? timeStamp : Calendar.getInstance().getTimeInMillis() - (seconds * 1000L);

		radius += 4;
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM player_move_events WHERE time>=?");
			statement.setLong(1, time);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (isLocationInReplay(eventLocation, replayLocation, radius)) {
					playerMoveEvents.add(new PlayerMoveEventContainer(UUID.fromString(results.getString("UUID")), eventLocation, results.getLong("time"), results.getString("name")));
				}
			}
			return playerMoveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerMoveEvents;
	}

	public ArrayList<DeathDamageEventContainer> getDeathDamageEvents(Location replayLocation, int radius, int seconds, long timeStamp) {
		ArrayList<DeathDamageEventContainer> deathDamageEvents = new ArrayList<>();
		if (replayLocation.getWorld() == null) return deathDamageEvents;

		long time = timeStamp > 1 ? timeStamp : Calendar.getInstance().getTimeInMillis() - (seconds * 1000L);

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM death_damage_events WHERE time>=?");
			statement.setLong(1, time);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (isLocationInReplay(eventLocation, replayLocation, radius)) {
					UUID uuid = UUID.fromString(results.getString("UUID"));
					eventLocation.setX((int) eventLocation.getX());
					eventLocation.setY((int) eventLocation.getY());
					eventLocation.setZ((int) eventLocation.getZ());

					deathDamageEvents.add(new DeathDamageEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("event_type"), results.getString("source")));
				}
			}
			return deathDamageEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deathDamageEvents;
	}

	public ArrayList<JoinLeaveEventContainer> getJoinLeaveEvents(Location replayLocation, int radius, int seconds, long timeStamp) {
		ArrayList<JoinLeaveEventContainer> joinLeaveEvents = new ArrayList<>();
		if (replayLocation.getWorld() == null) return joinLeaveEvents;

		long time = timeStamp > 1 ? timeStamp : Calendar.getInstance().getTimeInMillis() - (seconds * 1000L);

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM join_leave_events WHERE time>=?");
			statement.setLong(1, time);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (isLocationInReplay(eventLocation, replayLocation, radius)) {
					UUID uuid = UUID.fromString(results.getString("UUID"));
					eventLocation.setX((int) eventLocation.getX());
					eventLocation.setY((int) eventLocation.getY());
					eventLocation.setZ((int) eventLocation.getZ());

					joinLeaveEvents.add(new JoinLeaveEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("event_type")));
				}
			}
			return joinLeaveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return joinLeaveEvents;
	}

	public ArrayList<PlayerInventoryEventContainer> getPlayerInventoryEvents(Location replayLocation, int radius, int seconds, long timeStamp) {
		ArrayList<PlayerInventoryEventContainer> playerInventoryEvents = new ArrayList<>();
		if (replayLocation.getWorld() == null) return playerInventoryEvents;

		long time = timeStamp > 1 ? timeStamp : Calendar.getInstance().getTimeInMillis() - (seconds * 1000L);

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM player_inventory_events WHERE time>=?");
			statement.setLong(1, time);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (isLocationInReplay(eventLocation, replayLocation, radius)) {
					String[] serArr = results.getString("serialized").split(";");
					ItemStack[] content = InventorySerializer.itemStackArrayFromBase64(serArr[0]);
					ItemStack[] armour = InventorySerializer.itemStackArrayFromBase64(serArr[1]);
					int slot = Integer.parseInt(serArr[2]);
					ItemStack[] health = InventorySerializer.itemStackArrayFromBase64(serArr[3]);

					UUID uuid = UUID.fromString(results.getString("UUID"));
					eventLocation.setX((int) eventLocation.getX());
					eventLocation.setY((int) eventLocation.getY());
					eventLocation.setZ((int) eventLocation.getZ());

					playerInventoryEvents.add(new PlayerInventoryEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("serialized"), content, armour, health, slot));
				}
			}
			return playerInventoryEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerInventoryEvents;
	}

	private boolean isLocationInReplay(Location locationOne, Location locationTwo, int radius) {
		if (locationOne.getWorld() == null || locationTwo.getWorld() == null) return false;
		return (locationOne.getBlockX() >= locationTwo.getBlockX() - radius && locationOne.getBlockX() <= locationTwo.getBlockX() + radius)
				&& (locationOne.getBlockZ() >= locationTwo.getBlockZ() - radius && locationOne.getBlockZ() <= locationTwo.getBlockZ() + radius)
				&& locationOne.getWorld().getName().equals(locationTwo.getWorld().getName());

	}

	public void cleanTables() {
		try {
			for (String table : tables) {
				String tableName = table.split(" ")[0];
				PreparedStatement statement = MySQL.getInstance().getConnection().prepareStatement
						("DELETE FROM " + tableName + " WHERE time<=?");
				//3600000 is milliseconds in an hour
				statement.setLong(1, Calendar.getInstance().getTimeInMillis() - (3600000L * Config.getConfig().getLong("settings.hours-until-logs-deleted")));
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
