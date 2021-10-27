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
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
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
			"block_events (world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, old_block VARCHAR(500), new_block VARCHAR(500), time BIGINT)",
			"player_move_events (name VARCHAR(255), UUID VARCHAR(255), world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, time BIGINT)",
			"death_damage_events (name VARCHAR(255), UUID VARCHAR(255), world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, event_type VARCHAR(255), source VARCHAR(255), time BIGINT)",
			"player_inventory_events (name VARCHAR(255), UUID VARCHAR(255), world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, serialized MEDIUMTEXT, time BIGINT)",
			"join_leave_events (name VARCHAR(255), UUID VARCHAR(255), world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, event_type VARCHAR(255), time BIGINT)"
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
					("INSERT INTO block_events (world, x, y, z, old_block, new_block, time) VALUES (?, ?, ?, ?, ?, ?, ?)");
			statement.setString(1, blockEventObj.getWorld());
			statement.setDouble(2, blockEventObj.getX());
			statement.setDouble(3, blockEventObj.getY());
			statement.setDouble(4, blockEventObj.getZ());
			statement.setString(5, BlockChangeSerializer.serialize(blockEventObj.getOldBlock()));
			statement.setString(6, BlockChangeSerializer.serialize(blockEventObj.getNewBlock()));
			statement.setLong(7, blockEventObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logPlayerMoveEvent(PlayerMoveEventContainer playerMoveObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO player_move_events (name, UUID, world, x, y, z, yaw, pitch, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setString(1, playerMoveObj.getName());
			statement.setString(2, playerMoveObj.getUuid().toString());
			statement.setString(3, playerMoveObj.getWorld());
			statement.setDouble(4, playerMoveObj.getX());
			statement.setDouble(5, playerMoveObj.getY());
			statement.setDouble(6, playerMoveObj.getZ());
			statement.setFloat(7, playerMoveObj.getYaw());
			statement.setDouble(8, playerMoveObj.getPitch());
			statement.setLong(9, playerMoveObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logDeathDamageEvent(PlayerDeathDamageEventContainer deathDamageEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO death_damage_events (name, UUID, world, x, y, z, event_type, source, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setString(1, deathDamageEventObj.getName());
			statement.setString(2, deathDamageEventObj.getUuid().toString());
			statement.setString(3, deathDamageEventObj.getWorld());
			statement.setDouble(4, deathDamageEventObj.getX());
			statement.setDouble(5, deathDamageEventObj.getY());
			statement.setDouble(6, deathDamageEventObj.getZ());
			statement.setString(7, deathDamageEventObj.getType());
			statement.setString(8, deathDamageEventObj.getSource());
			statement.setLong(9, deathDamageEventObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logJoinLeaveEvent(PlayerJoinLeaveEventContainer joinLeaveEventObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO join_leave_events (name, UUID, world, x, y, z, event_type, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setString(1, joinLeaveEventObj.getName());
			statement.setString(2, joinLeaveEventObj.getUuid().toString());
			statement.setString(3, joinLeaveEventObj.getWorld());
			statement.setDouble(4, joinLeaveEventObj.getX());
			statement.setDouble(5, joinLeaveEventObj.getY());
			statement.setDouble(6, joinLeaveEventObj.getZ());
			statement.setString(7, joinLeaveEventObj.getType());
			statement.setLong(8, joinLeaveEventObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logPlayerInventoryEvent(PlayerInventoryEventContainer playerInventoryObj) {
		try {
			PreparedStatement statement = getConnection().prepareStatement
					("INSERT INTO player_inventory_events (name, UUID, world, x, y, z, serialized, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setString(1, playerInventoryObj.getName());
			statement.setString(2, playerInventoryObj.getUuid().toString());
			statement.setString(3, playerInventoryObj.getWorld());
			statement.setDouble(4, playerInventoryObj.getX());
			statement.setDouble(5, playerInventoryObj.getY());
			statement.setDouble(6, playerInventoryObj.getZ());
			statement.setString(7, playerInventoryObj.getSerializedInventory());
			statement.setLong(8, playerInventoryObj.getTime());

			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLocationQuery(ReplayContext context, int radius) {
		Location replayLocation = context.getLocation();
		int lowerX = replayLocation.getBlockX() - radius;
		int lowerY = replayLocation.getBlockY() - radius;
		int lowerZ = replayLocation.getBlockZ() - radius;
		int upperX = replayLocation.getBlockX() + radius;
		int upperY = replayLocation.getBlockY() + radius;
		int upperZ = replayLocation.getBlockZ() + radius;
		String worldQuery = "(world = '" + Objects.requireNonNull(replayLocation.getWorld()).getName() + "')";
		String xQuery = "(x >= " + lowerX + " AND x <= " + upperX + ")";
		String yQuery = "(y >= " + lowerY + " AND y <= " + upperY + ")";
		String zQuery = "(z >= " + lowerZ + " AND z <= " + upperZ + ")";
		return "AND " + worldQuery + " AND " + xQuery + " AND " + (Config.getConfig().getBoolean("settings.ignore-y-radius") ? "" : yQuery + " AND ") + zQuery;
	}

	public List<PlayerChangeBlockEventContainer> getBlockEvents(ReplayContext context) {
		List<PlayerChangeBlockEventContainer> blockEvents = new ArrayList<>();
		if (context.getLocation().getWorld() == null) return blockEvents;

		try {
			PreparedStatement statement = getConnection().prepareStatement
					("SELECT * FROM block_events WHERE time>=? AND time<=? " + getLocationQuery(context, context.getRadius()) + " ORDER BY time ASC LIMIT " + eventRenderBuffer);
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				try {
					Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
					BlockChange newBlock = BlockChangeSerializer.deserialize(results.getString("new_block"));
					BlockChange oldBlock = BlockChangeSerializer.deserialize(results.getString("old_block"));
					blockEvents.add(new PlayerChangeBlockEventContainer(UUID.randomUUID(), location, results.getLong("time"), newBlock, oldBlock));
				} catch (BlockChangeParseException e) {
					Player player = Bukkit.getPlayer(context.getViewer());
					if (player != null) {
						player.sendMessage(Config.readColouredString("block-change-event-parse-error"));
					}
					ConsoleLogger.getInstance().log(Level.SEVERE, Config.readColouredString("block-change-event-parse-error"));
					return blockEvents;
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
					("SELECT * FROM player_move_events WHERE time>=? AND time<=? " + getLocationQuery(context, radius) + " ORDER BY time ASC LIMIT " + eventRenderBuffer);
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"), results.getFloat("yaw"), results.getFloat("pitch"));
				playerMoveEvents.add(new PlayerMoveEventContainer(UUID.fromString(results.getString("UUID")), location, results.getLong("time"), results.getString("name")));
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
					("SELECT * FROM death_damage_events WHERE time>=? AND time<=? " + getLocationQuery(context, context.getRadius()) + " ORDER BY time ASC LIMIT " + eventRenderBuffer);
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
				UUID uuid = UUID.fromString(results.getString("UUID"));
				deathDamageEvents.add(new PlayerDeathDamageEventContainer(uuid, location, results.getLong("time"), results.getString("name"), results.getString("event_type"), results.getString("source")));
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
					("SELECT * FROM join_leave_events WHERE time>=? AND time<=? " + getLocationQuery(context, context.getRadius()) + " ORDER BY time ASC LIMIT " + eventRenderBuffer);
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
				UUID uuid = UUID.fromString(results.getString("UUID"));
				joinLeaveEvents.add(new PlayerJoinLeaveEventContainer(uuid, location, results.getLong("time"), results.getString("name"), results.getString("event_type")));
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
					("SELECT * FROM player_inventory_events WHERE time>=? AND time<=? " + getLocationQuery(context, context.getRadius()) + " ORDER BY time ASC LIMIT " + eventRenderBuffer);
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String[] serArr = results.getString("serialized").split(";");
				ItemStack[] content = InventorySerializer.itemStackArrayFromBase64(serArr[0]);
				ItemStack[] armour = InventorySerializer.itemStackArrayFromBase64(serArr[1]);
				int health = Integer.parseInt(serArr[2]);
				ItemStack[] hands = InventorySerializer.itemStackArrayFromBase64(serArr[3]);

				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
				UUID uuid = UUID.fromString(results.getString("UUID"));
				playerInventoryEvents.add(new PlayerInventoryEventContainer(uuid, location, results.getLong("time"), results.getString("name"), results.getString("serialized"), content, armour, hands, health));
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
					("SELECT name, player_move_events.uuid, world, x, y, z, yaw, pitch, time FROM player_move_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM player_move_events" +
							" WHERE time < ? " + getLocationQuery(context, radius) + " GROUP BY uuid)" +
							" ms ON player_move_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"), results.getFloat("yaw"), results.getFloat("pitch"));
				playerMoveEvents.add(new PlayerMoveEventContainer(UUID.fromString(results.getString("UUID")), location, results.getLong("time"), results.getString("name")));
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
					("SELECT name, death_damage_events.uuid, world, x, y, z, event_type, source, time FROM death_damage_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM death_damage_events" +
							" WHERE time < ? AND event_type=? " + getLocationQuery(context, context.getRadius()) + " GROUP BY uuid)" +
							" ms ON death_damage_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());
			statement.setString(2, "DEATH");

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
				UUID uuid = UUID.fromString(results.getString("UUID"));
				deathEvents.add(new PlayerDeathDamageEventContainer(uuid, location, results.getLong("time"), results.getString("name"), results.getString("event_type"), results.getString("source")));
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
					("SELECT name, join_leave_events.uuid, world, x, y, z, event_type, time FROM join_leave_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM join_leave_events" +
							" WHERE time < ? AND event_type=? " + getLocationQuery(context, context.getRadius()) + " GROUP BY uuid)" +
							" ms ON join_leave_events.uuid = ms.uuid AND time = maxtime");
			statement.setLong(1, context.getStartTimestamp());
			statement.setString(2, "LEAVE");

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
				UUID uuid = UUID.fromString(results.getString("UUID"));
				leaveEvents.add(new PlayerJoinLeaveEventContainer(uuid, location, results.getLong("time"), results.getString("name"), results.getString("event_type")));
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
					("SELECT name, player_inventory_events.uuid, world, x, y, z, serialized, time FROM player_inventory_events" +
							" INNER JOIN (SELECT uuid, MAX(time) AS maxtime FROM player_inventory_events" +
							" WHERE time < ? GROUP BY uuid)" +
							" ms ON player_inventory_events.uuid = ms.uuid AND time = maxtime" +
							" WHERE ms.uuid IN (SELECT uuid FROM player_move_events WHERE time>=? AND time<=? "
							+ getLocationQuery(context, context.getRadius() + 4) + " ORDER BY time ASC)");
			statement.setLong(1, context.getStartTimestamp());
			statement.setLong(2, context.getStartTimestamp());
			statement.setLong(3, context.getTimeOfCommand());

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				String[] serArr = results.getString("serialized").split(";");
				ItemStack[] content = InventorySerializer.itemStackArrayFromBase64(serArr[0]);
				ItemStack[] armour = InventorySerializer.itemStackArrayFromBase64(serArr[1]);
				int health = Integer.parseInt(serArr[2]);
				ItemStack[] hands = InventorySerializer.itemStackArrayFromBase64(serArr[3]);

				Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
				UUID uuid = UUID.fromString(results.getString("UUID"));
				playerInventoryEvents.add(new PlayerInventoryEventContainer(uuid, location, results.getLong("time"), results.getString("name"), results.getString("serialized"), content, armour, hands, health));
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
				try {
					Location location = new Location(Bukkit.getWorld(results.getString("world")), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
					BlockChange newBlock = BlockChangeSerializer.deserialize(results.getString("new_block"));
					BlockChange oldBlock = BlockChangeSerializer.deserialize(results.getString("old_block"));
					playerChangeBlockEventContainerRenderer.undoRender(new PlayerChangeBlockEventContainer(UUID.randomUUID(), location, results.getLong("time"), newBlock, oldBlock));
				} catch (BlockChangeParseException e) {
					Player player = Bukkit.getPlayer(context.getViewer());
					if (player != null) {
						player.sendMessage(Config.readColouredString("block-change-event-parse-error"));
					}
					ConsoleLogger.getInstance().log(Level.SEVERE, Config.readColouredString("block-change-event-parse-error"));
					return;
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
