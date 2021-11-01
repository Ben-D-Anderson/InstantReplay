package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerJoinLeaveEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.List;

public class PlayerJoinLeaveEventContainerProvider implements EventContainerProvider<PlayerJoinLeaveEventContainer> {

    @Override
    public List<PlayerJoinLeaveEventContainer> getEventContainers(ReplayContext context, boolean firstRequest) {
        return MySQL.getInstance().getJoinLeaveEvents(context);
    }

}
