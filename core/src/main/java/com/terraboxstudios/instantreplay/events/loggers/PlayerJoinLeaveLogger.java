package com.terraboxstudios.instantreplay.events.loggers;

import com.terraboxstudios.instantreplay.events.containers.PlayerJoinLeaveEventContainer;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Calendar;

public class PlayerJoinLeaveLogger implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        new PlayerJoinLeaveEventContainer(
                p.getUniqueId(),
                p.getLocation(),
                Calendar.getInstance().getTimeInMillis(),
                p.getName(),
                "JOIN"
        ).log();
        new PlayerMoveEventContainer(
                p.getUniqueId(),
                p.getLocation(),
                Calendar.getInstance().getTimeInMillis(),
                p.getName()
        ).log();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        new PlayerJoinLeaveEventContainer(
                p.getUniqueId(),
                p.getLocation(),
                Calendar.getInstance().getTimeInMillis(),
                p.getName(),
                "LEAVE"
        ).log();
    }

}
