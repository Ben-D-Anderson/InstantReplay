package com.terraboxstudios.instantreplay.events.loggers;

import com.terraboxstudios.instantreplay.InstantReplay;
import com.terraboxstudios.instantreplay.events.containers.PlayerChangeBlockEventContainer;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeData;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeFactory;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Calendar;
import java.util.UUID;

public class PlayerChangeBlockLogger implements Listener {

    private final BlockChangeFactory blockChangeFactory;

    public PlayerChangeBlockLogger() {
        blockChangeFactory = InstantReplay.getVersionSpecificProvider().getBlockChangeFactory();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Location location = e.getBlock().getLocation();
        BlockChangeData<?> newBlockChangeData = blockChangeFactory.createBlockChangeData(e.getBlock());
        BlockChange newBlockChange = blockChangeFactory.createBlockChange(newBlockChangeData);
        BlockChangeData<?> oldBlockChangeData = blockChangeFactory.createEmptyBlockChangeData();
        BlockChange oldBlockChange = blockChangeFactory.createBlockChange(oldBlockChangeData);
        new PlayerChangeBlockEventContainer(
                UUID.randomUUID(),
                location,
                Calendar.getInstance().getTimeInMillis(),
                newBlockChange,
                oldBlockChange
        ).log();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Location location = e.getBlock().getLocation();
        BlockChangeData<?> newBlockChangeData = blockChangeFactory.createEmptyBlockChangeData();
        BlockChange newBlockChange = blockChangeFactory.createBlockChange(newBlockChangeData);
        BlockChangeData<?> oldBlockChangeData = blockChangeFactory.createBlockChangeData(e.getBlock());
        BlockChange oldBlockChange = blockChangeFactory.createBlockChange(oldBlockChangeData);
        new PlayerChangeBlockEventContainer(
                UUID.randomUUID(),
                location,
                Calendar.getInstance().getTimeInMillis(),
                newBlockChange,
                oldBlockChange
        ).log();
    }

}
