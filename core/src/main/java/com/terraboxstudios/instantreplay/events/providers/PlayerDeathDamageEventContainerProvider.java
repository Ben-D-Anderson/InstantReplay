package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerDeathDamageEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public class PlayerDeathDamageEventContainerProvider implements EventContainerProvider<PlayerDeathDamageEventContainer> {

    @Override
    public List<PlayerDeathDamageEventContainer> getEventContainers(Location replayLocation, int radius, long timestamp) {
        return MySQL.getInstance().getDeathDamageEvents(replayLocation, radius, timestamp);
    }

}
