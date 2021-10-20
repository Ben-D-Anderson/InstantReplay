package com.terraboxstudios.instantreplay.listeners;

import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (ReplayThreads.isUserReplaying(p.getUniqueId())) {
			e.setCancelled(true);
		}
	}

}
