package com.terraboxstudios.instantreplay.events.loggers;

import com.terraboxstudios.instantreplay.InstantReplay;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.inventory.InventorySerializer;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInventoryLoggingService {

	private final HashMap<UUID, String[]> invCache = new HashMap<>();

	private double getSecondsPerLog() {
		return Config.getConfig().getDouble("settings.seconds-per-player-inventory-log");
	}

	public PlayerInventoryLoggingService() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(InstantReplay.getPlugin(InstantReplay.class), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.isDead()) continue;

				ItemStack[] handsArr = InstantReplay.getVersionSpecificProvider().getItemFactory().getHands(player.getInventory());
				String[] inv = InventorySerializer.playerInventoryToBase64(player.getInventory(), handsArr);

				String[] cachedInventory = invCache.get(player.getUniqueId());
				if (cachedInventory != null && Arrays.equals(invCache.get(player.getUniqueId()), inv)) {
					continue;
				}

				invCache.put(player.getUniqueId(), inv);
				String serializedInventory = inv[0] + ";" + inv[1] + ";" + inv[2] + ";" + inv[3];
				Location location = player.getLocation().clone();
				location.setX((int) location.getX());
				location.setY((int) location.getY());
				location.setZ((int) location.getZ());

				new PlayerInventoryEventContainer(
						player.getUniqueId(),
						location,
						Calendar.getInstance().getTimeInMillis(),
						player.getName(),
						serializedInventory,
						player.getInventory().getContents(),
						player.getInventory().getArmorContents(),
						handsArr,
						(int) player.getHealth()
				).log();
			}
		}, 20L, (long) (getSecondsPerLog() * 20L));
	}

}
