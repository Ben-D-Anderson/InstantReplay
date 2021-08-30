package com.terraboxstudios.instantreplay.commands;

import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayInstance;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ReplayCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		long timeOfCommandRun = Calendar.getInstance().getTimeInMillis() + 100;

		if (args.length == 0) {
			sender.sendMessage(Config.readColouredStringList("invalid-argument"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(Config.readColouredStringList("invalid-argument"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-reload-permission")))) {
				sender.sendMessage(Config.readColouredString("no-permission"));
				return true;
			}
			Config.reloadConfig();
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("config-reloaded"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("clearlogs")) {
			if (!sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-clearlogs-permission")))) {
				sender.sendMessage(Config.readColouredString("no-permission"));
				return true;
			}
			MySQL.getInstance().clearLogs();
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("logs-cleared"));
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Command Only Allowed For Players");
			return true;
		}
		Player player = (Player) sender;
		
		if (!player.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-permission")))) {
			sender.sendMessage(Config.readColouredString("no-permission"));
			return true;
		}


		if (args[0].equalsIgnoreCase("stop")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				ReplayThreads.stopThread(player.getUniqueId());
				player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-stopped"));
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
			}
			return true;
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
				ReplayThreads.getThread(player.getUniqueId()).getContext().setSpeed(speed);
				player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-speed-changed").replace("{SPEED}", speed + ""));
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("pause")) {
			if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
				if (ReplayThreads.getThread(player.getUniqueId()).getPlaying().get()) {
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
				if (!ReplayThreads.getThread(player.getUniqueId()).getPlaying().get()) {
					ReplayThreads.getThread(player.getUniqueId()).resumeReplay();
					player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-resumed"));
				} else {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-not-paused"));
				}
			} else {
				sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
			}
			return true;
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
				} catch (Exception e2) {
					sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
					return true;
				}
			} else {
				timeStamp = Calendar.getInstance().getTimeInMillis() - (time * 1000L);
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

		ReplayContext context = new ReplayContext.Builder(
				player.getUniqueId(),
				timeStamp,
				radius,
				player.getLocation()
		).setSpeed(speed).build();
		ReplayInstance replayInstance = new ReplayInstance(context);
		ReplayThreads.addToThreads(player.getUniqueId(), replayInstance);
		return true;
	}

}
