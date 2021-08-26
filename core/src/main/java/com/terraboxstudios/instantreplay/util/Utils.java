package com.terraboxstudios.instantreplay.util;

import com.terraboxstudios.instantreplay.containers.BlockEventContainer;
import com.terraboxstudios.instantreplay.containers.PlayerMoveEventContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class Utils {

	public static Location stringToLocation(String str) {
		String[] str2loc = str.split(":");
		Location loc = new Location(Bukkit.getServer().getWorld(str2loc[0]),0,0,0);
		loc.setX(Double.parseDouble(str2loc[1]));
		loc.setY(Double.parseDouble(str2loc[2]));
		loc.setZ(Double.parseDouble(str2loc[3]));
		loc.setYaw(Float.parseFloat(str2loc[4]));
		loc.setPitch(Float.parseFloat(str2loc[5]));
		return loc;
	}

	public static String locationToString(Location loc) {
		return Objects.requireNonNull(loc.getWorld()).getName()+":"+loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ()+":"+loc.getYaw()+":"+loc.getPitch();
	}

	public static String preciseLocationToString(Location loc) {
		return Objects.requireNonNull(loc.getWorld()).getName()+":"+loc.getX()+":"+loc.getY()+":"+loc.getZ()+":"+loc.getYaw()+":"+loc.getPitch();
	}

	public static void sortBlockEventsByTime(ArrayList<BlockEventContainer> blockEvents) {
		blockEvents.sort(Comparator.comparingLong(BlockEventContainer::getTime));
	}

	public static ArrayList<PlayerMoveEventContainer> sortPlayerMoveEventsByTime(ArrayList<PlayerMoveEventContainer> playerMoveEvents) {
		playerMoveEvents.sort(Comparator.comparingLong(PlayerMoveEventContainer::getTime));
		return playerMoveEvents;
	}

	public static List<PlayerMoveEventContainer> sortPlayerMoveEventsByTime(List<PlayerMoveEventContainer> playerMoveEvents) {
		playerMoveEvents.sort(Comparator.comparingLong(PlayerMoveEventContainer::getTime));
		return playerMoveEvents;
	}

	public static ArrayList<ArrayList<PlayerMoveEventContainer>> sortPlayerMoveEventsByPlayer(ArrayList<PlayerMoveEventContainer> playerEvents) {
		HashMap<String, ArrayList<PlayerMoveEventContainer>> knownPlayers = new HashMap<>();
		for (PlayerMoveEventContainer playerMoveObj : playerEvents) {
			knownPlayers.computeIfAbsent(playerMoveObj.getName(), k -> new ArrayList<>());
			knownPlayers.get(playerMoveObj.getName()).add(playerMoveObj);
		}
		ArrayList<ArrayList<PlayerMoveEventContainer>> finalArrayList = new ArrayList<>();

		for (String key : knownPlayers.keySet()) {
			finalArrayList.add(knownPlayers.get(key));
		}

		return finalArrayList;
	}

	public static String getReplayPrefix() {
		return Config.getConfig().getBoolean("settings.use-plugin-prefix") ? Config.readColouredString("plugin-prefix") : "";
	}
	
}
