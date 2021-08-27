package com.terraboxstudios.instantreplay.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.replay.ReplayInstance;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class RightClickNPCEvent implements Listener {

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}
		//todo check if inventory is an npc inventory
		if (e.getInventory().getName().contains("'s Inventory")) {
			e.setCancelled(true);
		}
	}

	public RightClickNPCEvent() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		registerListener(protocolManager);
	}

	private void registerListener(ProtocolManager protocolManager) {
		protocolManager.addPacketListener(new PacketAdapter(Main.getPlugin(Main.class), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			public void onPacketReceiving(PacketEvent e) {
				if (!(e.getPacketType() == PacketType.Play.Client.USE_ENTITY && ReplayThreads.isUserReplaying(e.getPlayer().getUniqueId())))
					return;

				PacketContainer packet = e.getPacket();
				Player player = e.getPlayer();

				ReplayInstance replayInstance = ReplayThreads.getThread(e.getPlayer().getUniqueId());
				if (replayInstance == null) return;

				Optional<NPC> clickedNPCOptional = findNPCById(replayInstance, packet.getIntegers().read(0));
				if (!clickedNPCOptional.isPresent()) return;
				NPC clickedNPC = clickedNPCOptional.get();

				player.openInventory(replayInstance.getInventories().get(clickedNPC.getUniqueId()));
			}
		});
	}

	private Optional<NPC> findNPCById(ReplayInstance replayInstance, int id) {
		return replayInstance.getNpcs().values()
				.stream()
				.filter(npc -> npc.getId() == id)
				.findFirst();
	}

}
