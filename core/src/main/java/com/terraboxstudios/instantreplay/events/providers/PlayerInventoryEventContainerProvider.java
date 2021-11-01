package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.List;

public class PlayerInventoryEventContainerProvider implements EventContainerProvider<PlayerInventoryEventContainer> {

    @Override
    public List<PlayerInventoryEventContainer> getEventContainers(ReplayContext context, boolean firstRequest) {
        List<PlayerInventoryEventContainer> containers = MySQL.getInstance().getPlayerInventoryEvents(context);
        if (firstRequest) containers.addAll(calculatePreReplayEvents(context));
        return containers;
    }

    private List<PlayerInventoryEventContainer> calculatePreReplayEvents(ReplayContext context) {
        return MySQL.getInstance().getPreReplayInventoryEvents(context);
    }

}
