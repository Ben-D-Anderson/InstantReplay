package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerChangeBlockEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public class PlayerChangeBlockEventContainerProvider implements EventContainerProvider<PlayerChangeBlockEventContainer> {

    @Override
    public List<PlayerChangeBlockEventContainer> getEventContainers(Location replayLocation, int radius, long timestamp) {
        return MySQL.getInstance().getBlockEvents(replayLocation, radius, timestamp);
    }

}
