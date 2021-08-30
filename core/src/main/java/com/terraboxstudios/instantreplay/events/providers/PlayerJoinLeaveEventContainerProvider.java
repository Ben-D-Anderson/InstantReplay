package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerJoinLeaveEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import org.bukkit.Location;

import java.util.List;

public class PlayerJoinLeaveEventContainerProvider implements EventContainerProvider<PlayerJoinLeaveEventContainer> {

    @Override
    public List<PlayerJoinLeaveEventContainer> getEventContainers(Location replayLocation, int radius, long timestamp) {
        return MySQL.getInstance().getJoinLeaveEvents(replayLocation, radius, timestamp);
    }

}
