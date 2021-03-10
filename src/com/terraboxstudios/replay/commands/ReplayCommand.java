package com.terraboxstudios.replay.commands;

import com.terraboxstudios.replay.containers.*;
import com.terraboxstudios.replay.events.PlayerMoveLogger;
import com.terraboxstudios.replay.mysql.MySQL;
import com.terraboxstudios.replay.threads.ReplayInstance;
import com.terraboxstudios.replay.threads.ReplayThreads;
import com.terraboxstudios.replay.util.Config;
import com.terraboxstudios.replay.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ReplayCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		long timeOfCommandRun = Calendar.getInstance().getTime().getTime() + 100;

		if (args.length == 0) {
			sender.sendMessage(Config.readColouredStringList("invalid-argument"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(Config.readColouredStringList("invalid-argument"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission(Config.getConfig().getString("settings.replay-reload-permission"))) {
				sender.sendMessage(Config.readColouredString("no-permission"));
				return true;
			}
			Config.reloadConfig();
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("config-reloaded"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("clearlogs")) {
			if (!sender.hasPermission(Config.getConfig().getString("settings.replay-clearlogs-permission"))) {
				sender.sendMessage(Config.readColouredString("no-permission"));
				return true;
			}
			MySQL.clearLogs();
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("logs-cleared"));
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Command Only Allowed For Players");
			return true;
		}
		Player player = (Player) sender;
		
		if (!player.hasPermission(Config.getConfig().getString("settings.replay-permission"))) {
			sender.sendMessage(Config.readColouredString("no-permission"));
			return true;
		}


		if (args[0].equalsIgnoreCase("stop")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				ReplayThreads.stopThread(player.getUniqueId());
				player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-stopped"));
				return true;				
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("speed")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				if (args.length != 2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-speed"));
					return true;
				}
				int speed;
				try {
					speed = Integer.parseInt(args[1]);
				} catch (Exception e) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-speed"));
					return true;
				}
				ReplayThreads.getThread(player.getUniqueId()).setSpeed(speed);
				player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-speed-changed").replace("{SPEED}", speed + ""));
				return true;				
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("pause")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				if (ReplayThreads.getThread(player.getUniqueId()).isRunning()) {
					ReplayThreads.getThread(player.getUniqueId()).pauseReplay();
					player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-paused"));
				} else {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-already-paused"));
				}
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("resume")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				if (!ReplayThreads.getThread(player.getUniqueId()).isRunning()) {
					ReplayThreads.getThread(player.getUniqueId()).resumeReplay();
					player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-resumed"));
					return true;
				} else {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-not-paused"));
					return true;
				}
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("start")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-already-running"));
				return true;
			}
			if (args.length != 4) {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-start"));
				return true;				
			}
		}

		if (!args[0].equalsIgnoreCase("start") && !args[0].equalsIgnoreCase("resume") && !args[0].equalsIgnoreCase("pause") && !args[0].equalsIgnoreCase("stop") && !args[0].equalsIgnoreCase("reload") && !args[0].equalsIgnoreCase("clearlogs")) {
			sender.sendMessage(Config.readColouredStringList("invalid-argument"));
			return true;
		}

		int radius, time = 0, speed;
		long timeStamp = 0;
		try {
			time = Integer.parseInt(args[2]);
		} catch (Exception e) {
			if (!args[2].endsWith("s") && !args[2].endsWith("m") && !args[2].endsWith("h") && !args[2].endsWith("d") && !args[2].endsWith("t")) {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
				return true;
			}
			if (args[2].endsWith("s")) {
				try {
					time = Integer.parseInt(args[2].substring(0, args[2].length() - 1));
				} catch (Exception e2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
					return true;
				}
			}
			if (args[2].endsWith("m")) {
				try {
					time = 60 * Integer.parseInt(args[2].substring(0, args[2].length() - 1));
				} catch (Exception e2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
					return true;
				}
			}
			if (args[2].endsWith("h")) {
				try {
					time = 60 * 60 * Integer.parseInt(args[2].substring(0, args[2].length() - 1));
				} catch (Exception e2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
					return true;
				}
			}
			if (args[2].endsWith("d")) {
				try {
					time = 24 * 60 * 60 * Integer.parseInt(args[2].substring(0, args[2].length() - 1));
				} catch (Exception e2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
					return true;
				}
			}
			if (args[2].endsWith("t")) {
				try {
					timeStamp = Long.parseLong(args[2].substring(0, args[2].length() - 1));
					time = 0;
				} catch (Exception e2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
					return true;
				}
			}
		}
		try {
			radius = Integer.parseInt(args[1]);
			speed = Integer.parseInt(args[3]);
			if (speed < 1)
				speed = 1;
			if (radius < 1)
				radius = 1;
		} catch (Exception e) {
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start"));
			return true;
		}

		sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-loading"));

		ArrayList<PlayerMoveEventContainer> playerEvents = MySQL.getPlayerMoveEvents(player.getLocation(), radius, time, timeStamp);
		ArrayList<BlockEventContainer> blockEvents = MySQL.getBlockEvents(player.getLocation(), radius, time, timeStamp);
		ArrayList<DeathDamageEventContainer> deathDamageEvents = MySQL.getDeathDamageEvents(player.getLocation(), radius, time, timeStamp);
		ArrayList<JoinLeaveEventContainer> joinLeaveEvents = MySQL.getJoinLeaveEvents(player.getLocation(), radius, time, timeStamp);
		ArrayList<PlayerInventoryEventContainer> playerInventoryEvents = MySQL.getPlayerInventoryEvents(player.getLocation(), radius, time, timeStamp);
		if (blockEvents.isEmpty() && playerEvents.isEmpty() && deathDamageEvents.isEmpty() && joinLeaveEvents.isEmpty()) {
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-events-found"));
			return true;
		}

		List<PlayerMoveEventContainer> assumedPlayerMoveEvents = new LinkedList<>();
		if (!playerEvents.isEmpty()) {
			ArrayList<ArrayList<PlayerMoveEventContainer>> allPlayerMoveEvents = Utils.sortPlayerMoveEventsByPlayer(playerEvents);
			for (ArrayList<PlayerMoveEventContainer> playerMoveEventsTMP : allPlayerMoveEvents) {
				ArrayList<PlayerMoveEventContainer> playerMoveEvents = Utils.sortPlayerMoveEventsByTime(playerMoveEventsTMP);
				for (int i = 0; i < playerMoveEvents.size(); i++) {
					if (playerMoveEvents.get(i) == playerMoveEvents.get(playerMoveEvents.size() - 1)) {
						assumedPlayerMoveEvents.add(playerMoveEvents.get(i));
						continue;
					}
					assumedPlayerMoveEvents.add(playerMoveEvents.get(i));

					PlayerMoveEventContainer currentObj = playerMoveEvents.get(i);
					PlayerMoveEventContainer nextObj = playerMoveEvents.get(i + 1);
					Location currentLoc = currentObj.getLocation();
					Location nextLoc = nextObj.getLocation();

					double xChange = nextLoc.getX() - currentLoc.getX();
					double yChange = nextLoc.getY() - currentLoc.getY();
					double zChange = nextLoc.getZ() - currentLoc.getZ();

					double xIncrement = xChange / (PlayerMoveLogger.getSecondsPerLog() * 10);
					double yIncrement = yChange / (PlayerMoveLogger.getSecondsPerLog() * 10);
					double zIncrement = zChange / (PlayerMoveLogger.getSecondsPerLog() * 10);

					if (xIncrement == 0 && yIncrement == 0 && zIncrement == 0) {
						continue;
					}

					for (int x = 0; x < PlayerMoveLogger.getSecondsPerLog() * 10; x++) {						
						currentLoc.setX(currentLoc.getX() + xIncrement);
						currentLoc.setY(currentLoc.getY() + yIncrement);
						currentLoc.setZ(currentLoc.getZ() + zIncrement);

						currentObj.setTime(currentObj.getTime() + 100L);

						assumedPlayerMoveEvents.add(new PlayerMoveEventContainer(currentObj.getName(), currentObj.getUuid(), currentLoc.getWorld().getName(), currentLoc.getX(), currentLoc.getY(), currentLoc.getZ(), currentLoc.getYaw(), currentLoc.getPitch(), currentObj.getTime()));				
					}
				}
			}

			for (PlayerMoveEventContainer playerMoveObj : assumedPlayerMoveEvents) {
				playerMoveObj.setTime(Long.parseLong(new DecimalFormat("#").format(playerMoveObj.getTime() / 100)) * 100);
			}
		}

		if (!blockEvents.isEmpty()) {
			Utils.sortBlockEventsByTime(blockEvents);
			for(BlockEventContainer blockEventObj : blockEvents) {
				player.sendBlockChange(blockEventObj.getLoc(), blockEventObj.getOldBlockMaterial(), blockEventObj.getOldBlockData());
				blockEventObj.setTime(Long.parseLong(new DecimalFormat("#").format(blockEventObj.getTime() / 100)) * 100);
			}
		}

		if (!deathDamageEvents.isEmpty()) {
			for(DeathDamageEventContainer deathDamageEventObj : deathDamageEvents) {
				deathDamageEventObj.setTime((Long.parseLong(new DecimalFormat("#").format(deathDamageEventObj.getTime() / 100)) * 100));
			}
		}

		if (!joinLeaveEvents.isEmpty()) {
			for(JoinLeaveEventContainer joinLeaveEventObj : joinLeaveEvents) {
				joinLeaveEventObj.setTime((Long.parseLong(new DecimalFormat("#").format(joinLeaveEventObj.getTime() / 100)) * 100));
			}
		}

		if (!playerInventoryEvents.isEmpty()) {
			for (PlayerInventoryEventContainer playerInventoryObj : playerInventoryEvents) {
				playerInventoryObj.setTime((Long.parseLong(new DecimalFormat("#").format(playerInventoryObj.getTime() / 100)) * 100));
			}
		}

		ReplayThreads.addToThreads(player.getUniqueId(), new ReplayInstance(blockEvents, assumedPlayerMoveEvents, deathDamageEvents, joinLeaveEvents, playerInventoryEvents, player.getUniqueId(), speed, time, timeStamp, timeOfCommandRun, radius, player.getLocation()));

		return true;
	}

}
