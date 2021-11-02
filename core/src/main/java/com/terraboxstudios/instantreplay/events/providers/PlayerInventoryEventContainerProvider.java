package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerInventoryEventContainerProvider implements EventContainerProvider<PlayerInventoryEventContainer> {

    @Override
    public List<PlayerInventoryEventContainer> getEventContainers(ReplayContext context, boolean firstRequest) {
        List<PlayerInventoryEventContainer> containers = MySQL.getInstance().getPlayerInventoryEvents(context);
        if (firstRequest) calculatePreReplayEvents(context).forEach(pre -> insertContainer(containers, pre));
        return containers;
    }

    private List<PlayerInventoryEventContainer> calculatePreReplayEvents(ReplayContext context) {
        return MySQL.getInstance().getPreReplayInventoryEvents(context);
    }

    private void insertContainer(List<PlayerInventoryEventContainer> containers, PlayerInventoryEventContainer container) {
        int index = Collections.binarySearch(containers, container, Comparator.comparing(EventContainer::getTime));
        if (index < 0) {
            index = -index - 1;
        }
        containers.add(index, container);
    }

}
