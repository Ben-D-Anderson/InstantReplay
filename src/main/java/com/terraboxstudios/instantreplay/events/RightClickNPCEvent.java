package com.terraboxstudios.instantreplay.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.threads.ReplayInstance;
import com.terraboxstudios.instantreplay.threads.ReplayThreads;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class RightClickNPCEvent implements Listener {

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}
		if (e.getInventory().getName().contains("'s Inventory")) {
			e.setCancelled(true);
		}
	}
	
	public RightClickNPCEvent() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(new PacketAdapter(Main.getPlugin(Main.class), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY){
			public void onPacketReceiving(PacketEvent e){
				if (e.getPacketType() == PacketType.Play.Client.USE_ENTITY && ReplayThreads.isUserReplaying(e.getPlayer().getUniqueId())){
					try {
						PacketContainer packet = e.getPacket();
						Player player = e.getPlayer();

						ReplayInstance threadObj = ReplayThreads.getThread(e.getPlayer().getUniqueId());
						for (UUID uuid : threadObj.getNpcs().keySet()) {
							if (threadObj.getNpcs().get(uuid).getId() == packet.getIntegers().read(0)) {
								EntityPlayer clickedNPC = threadObj.getNpcs().get(uuid);
								if (threadObj.getInventories().get(uuid) == null) {
									Inventory inv = Bukkit.createInventory(null, 45, clickedNPC.getName() + "'s Inventory");
									threadObj.getInventories().put(uuid, inv);
								}
								for (int i = 0; i < 9; i++) {
									ItemStack item = threadObj.getContentAndArmour().get(uuid).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									threadObj.getInventories().get(uuid).setItem(i, item);
								}
								for (int i = 9; i < 18; i++) {
									ItemStack item = threadObj.getContentAndArmour().get(uuid).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									threadObj.getInventories().get(uuid).setItem(i + 18, item);
								}
								for (int i = 18; i < 27; i++) {
									ItemStack item = threadObj.getContentAndArmour().get(uuid).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									threadObj.getInventories().get(uuid).setItem(i, item);
								}
								for (int i = 27; i < 36; i++) {
									ItemStack item = threadObj.getContentAndArmour().get(uuid).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									threadObj.getInventories().get(uuid).setItem(i - 18, item);
								}
								for (int i = 36; i < 40; i++) {
									ItemStack item = threadObj.getContentAndArmour().get(uuid).getArmourContents()[i - 36];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									threadObj.getInventories().get(uuid).setItem(i, item);
								}
								ItemStack healthItem = threadObj.getContentAndArmour().get(uuid).getHealth()[0];
								if (healthItem == null)
									healthItem = new ItemStack(Material.AIR, 1);
								try {
									threadObj.getInventories().get(uuid).setItem(40, healthItem);
								}  catch (NullPointerException ignored) {}
								for (int i = 41; i < 45; i++) {
									ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
									ItemMeta itemmeta = item.getItemMeta();
									itemmeta.setDisplayName(" ");
									item.setItemMeta(itemmeta);
									threadObj.getInventories().get(uuid).setItem(i, item);
								}
								player.openInventory(threadObj.getInventories().get(uuid));
							}
						}

					}  catch (Exception ignored) {}
				}
			}
		});
	}

}
