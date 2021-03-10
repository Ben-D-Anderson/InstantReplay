package com.terraboxstudios.replay.events;

import com.terraboxstudios.replay.containers.JoinLeaveEventContainer;
import com.terraboxstudios.replay.services.EventLoggingService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Calendar;

public class JoinLeaveEvent implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		EventLoggingService.getInstance().logEvent(new JoinLeaveEventContainer("JOIN", p.getName(), p.getUniqueId(), p.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), Calendar.getInstance().getTime().getTime()));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		EventLoggingService.getInstance().logEvent(new JoinLeaveEventContainer("LEAVE", p.getName(), p.getUniqueId(), p.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), Calendar.getInstance().getTime().getTime()));
	}
	
}
