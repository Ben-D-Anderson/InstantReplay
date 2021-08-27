package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.containers.BlockEventContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Calendar;
import java.util.UUID;

public class BlockChangeEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Location location = e.getBlock().getLocation();
        Material oldMaterial = Material.AIR;
        Material newMaterial = e.getBlock().getState().getType();
        byte oldBlockData = 0;
        byte newBlockData = e.getBlock().getState().getRawData();
        new BlockEventContainer(UUID.randomUUID(),
                location,
                Calendar.getInstance().getTimeInMillis(),
                oldMaterial,
                newMaterial,
                oldBlockData,
                newBlockData
        ).log();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Location location = e.getBlock().getLocation();
        Material oldMaterial = e.getBlock().getState().getType();
        Material newMaterial = Material.AIR;
        byte oldBlockData = e.getBlock().getState().getRawData();
        byte newBlockData = 0;
        new BlockEventContainer(UUID.randomUUID(),
                location,
                Calendar.getInstance().getTimeInMillis(),
                oldMaterial,
                newMaterial,
                oldBlockData,
                newBlockData
        ).log();
    }

}
