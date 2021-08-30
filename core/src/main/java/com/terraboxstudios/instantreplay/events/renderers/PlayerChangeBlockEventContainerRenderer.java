package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerChangeBlockEventContainer;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerChangeBlockEventContainerRenderer extends EventContainerRenderer<PlayerChangeBlockEventContainer> {

    public PlayerChangeBlockEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerChangeBlockEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    public void renderRemaining(long currentTimestamp) {
        if (getEventContainers().size() < 1) return;
        getEventContainers()
                .stream()
                .filter(container -> container.getTime() > currentTimestamp)
                .forEach(this::render);
    }

    @Override
    protected void render(PlayerChangeBlockEventContainer eventContainer) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;

        //todo version specific implementation
        player.sendBlockChange(eventContainer.getLocation(), eventContainer.getNewBlockMaterial(), eventContainer.getNewBlockData());
    }

}
