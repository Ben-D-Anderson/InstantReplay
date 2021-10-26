package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.List;

public class PlayerInventoryEventContainerProvider implements EventContainerProvider<PlayerInventoryEventContainer> {

    @Override
    public List<PlayerInventoryEventContainer> getEventContainers(ReplayContext context) {
        List<PlayerInventoryEventContainer> containers = MySQL.getInstance().getPlayerInventoryEvents(context);
        containers.addAll(calculatePreReplayEvents(context));
        return containers;
    }

    private List<PlayerInventoryEventContainer> calculatePreReplayEvents(ReplayContext context) {
        //should cross-reference with players who are actually in the event area in relay, but u will need to compare all
        //and the MySQL method for getting all events works with a buffer - otherwise you will get way too much data.
        //unsure of current solution - would be easier to handle everything in sql, but sadly we currently serialize location
        //solution is probably just to start storing x,y,z coords separately.
        return MySQL.getInstance().getPreReplayInventoryEvents(context);
    }

}
