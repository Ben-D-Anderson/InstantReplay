package com.terraboxstudios.instantreplay.events;

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void damageByEntityEvent(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;

        Location location = entity.getLocation();
        Player damaged = (Player) entity;

        String source = (e.getDamager() instanceof Player) ? "PLAYER (" + e.getDamager().getName() + ")" : e.getDamager().getType().toString();
        String type = (damaged.getHealth() - e.getDamage() <= 0) ? "DEATH" : "DAMAGE";

        new DeathDamageEventContainer(damaged.getUniqueId(),
                location,
                Calendar.getInstance().getTimeInMillis(),
                damaged.getName(),
                type,
                source
        ).log();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void damageEvent(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;

        DamageCause cause = e.getCause();
        if (cause == DamageCause.ENTITY_ATTACK) return;

        Location location = entity.getLocation();
        Player damaged = (Player) entity;

        String type = (damaged.getHealth() - e.getDamage() <= 0) ? "DEATH" : "DAMAGE";

        new DeathDamageEventContainer(
                damaged.getUniqueId(),
                location,
                Calendar.getInstance().getTimeInMillis(),
                damaged.getName(),
                type,
                cause.toString()
        ).log();
    }

}
