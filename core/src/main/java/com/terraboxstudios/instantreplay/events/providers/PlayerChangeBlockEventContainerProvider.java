package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerChangeBlockEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.List;

public class PlayerChangeBlockEventContainerProvider implements EventContainerProvider<PlayerChangeBlockEventContainer> {

    @Override
    public List<PlayerChangeBlockEventContainer> getEventContainers(ReplayContext context, boolean firstRequest) {
        return MySQL.getInstance().getBlockEvents(context);
    }

}
