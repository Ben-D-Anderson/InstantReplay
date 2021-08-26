package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.InventorySerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
				if (player.isDead())
					continue;
				int health = ((int) player.getHealth()) / 2;
				ItemStack h = new ItemStack(Material.SPECKLED_MELON);
				ItemMeta m = h.getItemMeta();
				m.setDisplayName(ChatColor.GREEN + "Health");
				List<String> lore = new LinkedList<>();
				lore.add(ChatColor.YELLOW + "" + health + ChatColor.RED + "❤");
				m.setLore(lore);
				h.setItemMeta(m);
				ItemStack[] healthArr = new ItemStack[] { h };
				String[] inv = InventorySerializer.playerInventoryToBase64(player.getInventory(), healthArr);
				if (invCache.get(player.getUniqueId()) != null) {
					if (!Arrays.equals(invCache.get(player.getUniqueId()), inv)) {
						invCache.put(player.getUniqueId(), inv);
						EventLoggingService.getInstance().logEvent(new PlayerInventoryEventContainer(player.getName(), player.getUniqueId(), inv[0] + ";" + inv[1] + ";" + inv[2] + ";" + inv[3], player.getInventory().getContents(), player.getInventory().getArmorContents(), healthArr, player.getInventory().getHeldItemSlot(), player.getLocation().getWorld().getName(), (int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ(), Calendar.getInstance().getTime().getTime()));
					}
				} else {
					invCache.put(player.getUniqueId(), inv);
					EventLoggingService.getInstance().logEvent(new PlayerInventoryEventContainer(player.getName(), player.getUniqueId(), inv[0] + ";" + inv[1] + ";" + inv[2] + ";" + inv[3], player.getInventory().getContents(), player.getInventory().getArmorContents(), healthArr, player.getInventory().getHeldItemSlot(), player.getLocation().getWorld().getName(), (int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ(), Calendar.getInstance().getTime().getTime()));
				}
			}
		}, 20L, getSecondsPerLog() * 20L);
	}

}
