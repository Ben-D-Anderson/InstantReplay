package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class PlayerMoveLogger {
	
	private final HashMap<UUID, Location> locCache = new HashMap<>();
	
	public static int getSecondsPerLog() {
		return Config.getConfig().getInt("settings.seconds-per-player-move-log");
	}
	
	public PlayerMoveLogger() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.isDead())
					if (locCache.get(player.getUniqueId()) != null) {
						if (!locCache.get(player.getUniqueId()).equals(player.getLocation())) {
							locCache.put(player.getUniqueId(), player.getLocation());
							EventLoggingService.getInstance().logEvent(new PlayerMoveEventContainer(player.getName(), player.getUniqueId(), player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), Calendar.getInstance().getTime().getTime()));
						}
					} else {
						locCache.put(player.getUniqueId(), player.getLocation());
						EventLoggingService.getInstance().logEvent(new PlayerMoveEventContainer(player.getName(), player.getUniqueId(), player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), Calendar.getInstance().getTime().getTime()));							
					}
			}
		}, 20L, getSecondsPerLog() * 20L);
	}
	
}
