package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import org.bukkit.Location;

import java.util.List;

public class PlayerMoveEventContainerProvider implements EventContainerProvider<PlayerMoveEventContainer> {

    @Override
    public List<PlayerMoveEventContainer> getEventContainers(Location replayLocation, int radius, long timestamp) {
        return MySQL.getInstance().getPlayerMoveEvents(replayLocation, radius, timestamp);
    }

}
