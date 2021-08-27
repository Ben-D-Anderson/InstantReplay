package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.inventory.InventorySerializer;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerInventoryLogger {

	private final HashMap<UUID, String[]> invCache = new HashMap<>();
	
	public static int getSecondsPerLog() {
		return Config.getConfig().getInt("settings.seconds-per-player-inventory-log");
	}

	public PlayerInventoryLogger() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.isDead()) continue;

				int health = ((int) player.getHealth()) / 2;
				ItemStack healthItem = Main.getVersionSpecificProvider().getItemFactory().getHealthItemGUI();
				ItemMeta healthItemMeta = healthItem.getItemMeta();
				if (healthItemMeta != null) {
					healthItemMeta.setDisplayName(ChatColor.GREEN + "Health");
					List<String> lore = new LinkedList<>();
					lore.add(ChatColor.YELLOW + "" + health + ChatColor.RED + "❤");
					healthItemMeta.setLore(lore);
					healthItem.setItemMeta(healthItemMeta);
				}

				ItemStack[] healthArr = new ItemStack[] { healthItem };
				String[] inv = InventorySerializer.playerInventoryToBase64(player.getInventory(), healthArr);

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
						healthArr,
						player.getInventory().getHeldItemSlot()
				).log();
			}
		}, 20L, getSecondsPerLog() * 20L);
	}

}
