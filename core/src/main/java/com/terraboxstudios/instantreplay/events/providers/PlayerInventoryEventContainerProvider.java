package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import org.bukkit.Location;

import java.util.List;

public class PlayerInventoryEventContainerProvider implements EventContainerProvider<PlayerInventoryEventContainer> {

    @Override
    public List<PlayerInventoryEventContainer> getEventContainers(Location replayLocation, int radius, long timestamp) {
        return MySQL.getInstance().getPlayerInventoryEvents(replayLocation, radius, timestamp);
    }

}
