package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.containers.DeathDamageEventContainer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Calendar;

public class DeathDamageEvent implements Listener {

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void damageByEntityEvent(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		if (!(entity instanceof Player))
			return;
		Location location = entity.getLocation();
		Player damaged = (Player) entity;
		String source;
		if (e.getDamager() instanceof Player) {
			source = "PLAYER (" + ((Player) e.getDamager()).getName() + ")";
		} else {
			source = e.getDamager().getType().toString();
		}
		if (damaged.getHealth() - e.getDamage() <= 0) {
			EventLoggingService.getInstance().logEvent(new DeathDamageEventContainer("DEATH", source, ((Player) entity).getName(), entity.getUniqueId(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), Calendar.getInstance().getTime().getTime()));
		} else {
			EventLoggingService.getInstance().logEvent(new DeathDamageEventContainer("DAMAGE", source, ((Player) entity).getName(), entity.getUniqueId(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), Calendar.getInstance().getTime().getTime()));
		}
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void damageEvent(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		DamageCause cause = e.getCause();
		if (cause == DamageCause.ENTITY_ATTACK) {
			return;
		}
		Location location = entity.getLocation();		
		Player damaged = (Player) entity;
		if (damaged.getHealth() - e.getDamage() <= 0) {
			EventLoggingService.getInstance().logEvent(new DeathDamageEventContainer("DEATH", cause.toString(), ((Player) entity).getName(), entity.getUniqueId(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), Calendar.getInstance().getTime().getTime()));
		} else {
			EventLoggingService.getInstance().logEvent(new DeathDamageEventContainer("DAMAGE", cause.toString(), ((Player) entity).getName(), entity.getUniqueId(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), Calendar.getInstance().getTime().getTime()));
		}
	}

}
