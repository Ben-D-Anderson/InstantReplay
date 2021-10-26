package com.terraboxstudios.instantreplay.mysql;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.events.containers.*;
import com.terraboxstudios.instantreplay.events.renderers.PlayerChangeBlockEventContainerRenderer;
import com.terraboxstudios.instantreplay.exceptions.BlockChangeParseException;
import com.terraboxstudios.instantreplay.inventory.InventorySerializer;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.util.BlockChangeSerializer;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.ConsoleLogger;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL {

	private Connection connection;
	@Getter
	private final int eventRenderBuffer;
	private final boolean couldConnect;
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
		eventRenderBuffer = Config.getConfig().getInt("settings.event-render-buffer");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" +
					port + "/" + database, username, password);
			initTables();
		} catch (ClassNotFoundException | SQLException exception) {
			ConsoleLogger.getInstance().log(Level.SEVERE, ChatColor.RED + "Failed to connect to MySQL database!");
			couldConnect = false;
			return;
		}
		couldConnect = true;
	}

	public boolean couldConnect() {
		return couldConnect;
	}

	private final String[] tables = {
			"block_events (location VARCHAR(255), old_block VARCHAR(500), new_block VARCHAR(500), time BIGINT)",
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

	public void clearLogs() {
		for (String table : tables) {
			String tableName = table.substring(0, table.indexOf("(")).trim();
			try {
				PreparedStatement statement = getConnection().prepareStatement("DELETE FROM " + tableName);
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void logEvent(EventContainer eventContainer) {
		if (eventContainer instanceof PlayerChangeBlockEventContainer) {
			logBlockEvent((PlayerChangeBlockEventContainer) eventContainer);
		} else if (eventContainer instanceof PlayerMoveEventContainer) {
			logPlayerMoveEvent((PlayerMoveEventContainer) eventContainer);
		} else if (eventContainer instanceof PlayerDeathDamageEventContainer) {
			logDeathDamageEvent((PlayerDeathDamageEventContainer) eventContainer);
		} else if (eventContainer instanceof PlayerJoinLeaveEventContainer) {
			logJoinLeaveEvent((PlayerJoinLeaveEventContainer) eventContainer);
		} else if (eventContainer instanceof PlayerInventoryEventContainer) {
			logPlayerInventoryEvent((PlayerInventoryEventContainer) eventContainer);
		} else {
			throw new IllegalArgumentException("Unsupported event container type.");
		}
	}

	private void logBlockEvent(PlayerChangeBlockEventContainer blockEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO block_events (location, old_block, new_block, time) VALUES (?, ?, ?, ?)");
			statement.setString(1, Utils.locationToString(blockEventObj.getLocation()));
			statement.setString(2, BlockChangeSerializer.serialize(blockEventObj.getOldBlock()));
			statement.setString(3, BlockChangeSerializer.serialize(blockEventObj.getNewBlock()));
			statement.setLong(4, blockEventObj.getTime());

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

	private void logDeathDamageEvent(PlayerDeathDamageEventContainer deathDamageEventObj) {
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

	private void logJoinLeaveEvent(PlayerJoinLeaveEventContainer joinLeaveEventObj) {
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

	public List<PlayerChangeBlockEventContainer> getBlockEvents(ReplayContext context) {
		List<PlayerChangeBlockEventContainer> blockEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return blockEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM block_events WHERE time>=? AND time<=? ORDER BY time ASC");
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				if (blockEvents.size() >= eventRenderBuffer)
					break;

				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					try {
						BlockChange newBlock = BlockChangeSerializer.deserialize(results.getString("new_block"));
						BlockChange oldBlock = BlockChangeSerializer.deserialize(results.getString("old_block"));
						blockEvents.add(new PlayerChangeBlockEventContainer(UUID.randomUUID(), eventLocation, results.getLong("time"), newBlock, oldBlock));
					} catch (BlockChangeParseException e) {
						Player player = Bukkit.getPlayer(context.getViewer());
						if (player != null) {
							player.sendMessage(Config.readColouredString("block-change-event-parse-error"));
						}
						ConsoleLogger.getInstance().log(Level.SEVERE, Config.readColouredString("block-change-event-parse-error"));
						return blockEvents;
					}
				}
			}
			return blockEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blockEvents;
	}

	public List<PlayerMoveEventContainer> getPlayerMoveEvents(ReplayContext context) {
		List<PlayerMoveEventContainer> playerMoveEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return playerMoveEvents;

		int radius = context.getRadius() + 4;
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM player_move_events WHERE time>=? AND time<=? ORDER BY time ASC");
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				if (playerMoveEvents.size() >= eventRenderBuffer)
					break;

				String locString = results.getString("location");
				Location eventLocation = Utils.stringToPreciseLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), radius)) {
					playerMoveEvents.add(new PlayerMoveEventContainer(UUID.fromString(results.getString("UUID")), eventLocation, results.getLong("time"), results.getString("name")));
				}
			}
			return playerMoveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerMoveEvents;
	}

	public List<PlayerDeathDamageEventContainer> getDeathDamageEvents(ReplayContext context) {
		List<PlayerDeathDamageEventContainer> deathDamageEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return deathDamageEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM death_damage_events WHERE time>=? AND time<=? ORDER BY time ASC");
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				if (deathDamageEvents.size() >= eventRenderBuffer)
					break;

				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					UUID uuid = UUID.fromString(results.getString("UUID"));
					deathDamageEvents.add(new PlayerDeathDamageEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("event_type"), results.getString("source")));
				}
			}
			return deathDamageEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deathDamageEvents;
	}

	public List<PlayerJoinLeaveEventContainer> getJoinLeaveEvents(ReplayContext context) {
		List<PlayerJoinLeaveEventContainer> joinLeaveEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return joinLeaveEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM join_leave_events WHERE time>=? AND time<=? ORDER BY time ASC");
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				if (joinLeaveEvents.size() >= eventRenderBuffer)
					break;

				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					UUID uuid = UUID.fromString(results.getString("UUID"));
					joinLeaveEvents.add(new PlayerJoinLeaveEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("event_type")));
				}
			}
			return joinLeaveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return joinLeaveEvents;
	}

	public List<PlayerInventoryEventContainer> getPlayerInventoryEvents(ReplayContext context) {
		List<PlayerInventoryEventContainer> playerInventoryEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return playerInventoryEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM player_inventory_events WHERE time>=? AND time<=? ORDER BY time ASC");
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				if (playerInventoryEvents.size() >= eventRenderBuffer)
					break;

				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					String[] serArr = results.getString("serialized").split(";");
					ItemStack[] content = InventorySerializer.itemStackArrayFromBase64(serArr[0]);
					ItemStack[] armour = InventorySerializer.itemStackArrayFromBase64(serArr[1]);
					int health = Integer.parseInt(serArr[2]);
					ItemStack[] hands = InventorySerializer.itemStackArrayFromBase64(serArr[3]);

					UUID uuid = UUID.fromString(results.getString("UUID"));
					playerInventoryEvents.add(new PlayerInventoryEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("serialized"), content, armour, hands, health));
				}
			}
			return playerInventoryEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerInventoryEvents;
	}

	public List<PlayerMoveEventContainer> getPreReplayPlayerMoveEvents(ReplayContext context) {
		List<PlayerMoveEventContainer> playerMoveEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return playerMoveEvents;

		int radius = context.getRadius() + 4;
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT name, player_move_events.uuid, location, time FROM player_move_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM player_move_events" +
							" WHERE time < ? GROUP BY uuid)" +
							" ms ON player_move_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToPreciseLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), radius)) {
					playerMoveEvents.add(new PlayerMoveEventContainer(UUID.fromString(results.getString("UUID")), eventLocation, results.getLong("time"), results.getString("name")));
				}
			}
			return playerMoveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerMoveEvents;
	}

	public List<PlayerDeathDamageEventContainer> getPreReplayDeathEvents(ReplayContext context) {
		List<PlayerDeathDamageEventContainer> deathEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return deathEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT name, death_damage_events.uuid, location, event_type, source, time FROM death_damage_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM death_damage_events" +
							" WHERE time < ? AND event_type=? GROUP BY uuid)" +
							" ms ON death_damage_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());
			statement.setString(2, "DEATH");

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					UUID uuid = UUID.fromString(results.getString("UUID"));
					deathEvents.add(new PlayerDeathDamageEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("event_type"), results.getString("source")));
				}
			}
			return deathEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deathEvents;
	}

	public List<PlayerJoinLeaveEventContainer> getPreReplayLeaveEvents(ReplayContext context) {
		List<PlayerJoinLeaveEventContainer> leaveEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return leaveEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT name, join_leave_events.uuid, location, event_type, time FROM join_leave_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM join_leave_events" +
							" WHERE time < ? AND event_type=? GROUP BY uuid)" +
							" ms ON join_leave_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());
			statement.setString(2, "LEAVE");

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					UUID uuid = UUID.fromString(results.getString("UUID"));
					leaveEvents.add(new PlayerJoinLeaveEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("event_type")));
				}
			}
			return leaveEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return leaveEvents;
	}

	public List<PlayerInventoryEventContainer> getPreReplayInventoryEvents(ReplayContext context) {
		List<PlayerInventoryEventContainer> playerInventoryEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return playerInventoryEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT name, player_inventory_events.uuid, location, serialized, time FROM player_inventory_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM player_inventory_events" +
							" WHERE time < ? GROUP BY uuid)" +
							" ms ON player_inventory_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);
				String[] serArr = results.getString("serialized").split(";");
				ItemStack[] content = InventorySerializer.itemStackArrayFromBase64(serArr[0]);
				ItemStack[] armour = InventorySerializer.itemStackArrayFromBase64(serArr[1]);
				int health = Integer.parseInt(serArr[2]);
				ItemStack[] hands = InventorySerializer.itemStackArrayFromBase64(serArr[3]);

				UUID uuid = UUID.fromString(results.getString("UUID"));
				playerInventoryEvents.add(new PlayerInventoryEventContainer(uuid, eventLocation, results.getLong("time"), results.getString("name"), results.getString("serialized"), content, armour, hands, health));
			}
			return playerInventoryEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerInventoryEvents;
	}

	public void undoRenderAllBlocks(PlayerChangeBlockEventContainerRenderer playerChangeBlockEventContainerRenderer, ReplayContext context) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM block_events WHERE time>=? ORDER BY time DESC");
			statement.setLong(1, context.getStartTimestamp());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String locString = results.getString("location");
				Location eventLocation = Utils.stringToLocation(locString);

				if (Utils.isLocationInReplay(eventLocation, context.getLocation(), context.getRadius())) {
					BlockChange newBlock = BlockChangeSerializer.deserialize(results.getString("new_block"));
					BlockChange oldBlock = BlockChangeSerializer.deserialize(results.getString("old_block"));
					playerChangeBlockEventContainerRenderer.undoRender(new PlayerChangeBlockEventContainer(UUID.randomUUID(), eventLocation, results.getLong("time"), newBlock, oldBlock));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cleanTables() {
		//3600000 is milliseconds in an hour
		long millisecondsUntilDeletion = 3600000L * Config.getConfig().getLong("settings.hours-until-logs-deleted");
		if (millisecondsUntilDeletion <= 0) return;
		try {
			for (String table : tables) {
				String tableName = table.split(" ")[0];
				PreparedStatement statement = MySQL.getInstance().getConnection().prepareStatement
						("DELETE FROM " + tableName + " WHERE time<=?");
				statement.setLong(1, Calendar.getInstance().getTimeInMillis() - millisecondsUntilDeletion);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (getConnection() != null) {
			try {
				getConnection().close();
			} catch (SQLException ignored) {
			}
		}
	}
}
