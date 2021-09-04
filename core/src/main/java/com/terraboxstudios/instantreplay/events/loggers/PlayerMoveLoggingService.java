package com.terraboxstudios.instantreplay.events.loggers;

import com.terraboxstudios.instantreplay.InstantReplay;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class PlayerMoveLoggingService {
	
	private final HashMap<UUID, Location> locCache = new HashMap<>();
	
	private double getSecondsPerLog() {
		return Config.getConfig().getDouble("settings.seconds-per-player-move-log");
	}
	
	public PlayerMoveLoggingService() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(InstantReplay.getPlugin(InstantReplay.class), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.isDead()) continue;

				Location cachedLoc = locCache.get(player.getUniqueId());
				if (cachedLoc != null && cachedLoc.equals(player.getLocation())) continue;

				locCache.put(player.getUniqueId(), player.getLocation());
				new PlayerMoveEventContainer(
						player.getUniqueId(),
						player.getLocation(),
						Calendar.getInstance().getTimeInMillis(),
						player.getName()
				).log();
			}
		}, 20L, (long) (getSecondsPerLog() * 20L));
	}
	
}