package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.containers.JoinLeaveEventContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Calendar;

public class JoinLeaveEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        new JoinLeaveEventContainer(
                p.getUniqueId(),
                p.getLocation(),
                Calendar.getInstance().getTimeInMillis(),
                p.getName(),
                "JOIN"
        ).log();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        new JoinLeaveEventContainer(
                p.getUniqueId(),
                p.getLocation(),
                Calendar.getInstance().getTimeInMillis(),
                p.getName(),
                "LEAVE"
        ).log();
    }

}
