package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.threads.ReplayThreads;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (ReplayThreads.isUserReplaying(p.getUniqueId())) {
			e.setCancelled(true);
		}
	}

}
