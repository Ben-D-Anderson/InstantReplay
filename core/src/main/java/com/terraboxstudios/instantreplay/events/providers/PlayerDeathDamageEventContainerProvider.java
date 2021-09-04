package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerDeathDamageEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.List;

public class PlayerDeathDamageEventContainerProvider implements EventContainerProvider<PlayerDeathDamageEventContainer> {

    @Override
    public List<PlayerDeathDamageEventContainer> getEventContainers(ReplayContext context) {
        return MySQL.getInstance().getDeathDamageEvents(context);
    }

}
