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
        if (getEventContainers().isEmpty()) return;
        getEventContainers()
                .stream()
                .filter(container -> container.getTime() > currentTimestamp)
                .forEach(this::render);
    }

    public void undoRenderAll() {
        getEventContainers().forEach(this::undoRender);
    }

    public void undoRender(PlayerChangeBlockEventContainer eventContainer) {
        render(eventContainer, true);
    }

    @Override
    protected void render(PlayerChangeBlockEventContainer eventContainer) {
        render(eventContainer, false);
    }

    private void render(PlayerChangeBlockEventContainer eventContainer, boolean undoRender) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;

        if (undoRender) {
            player.sendBlockChange(eventContainer.getLocation(), eventContainer.getOldBlockMaterial(), eventContainer.getOldBlockData());
        } else {
            player.sendBlockChange(eventContainer.getLocation(), eventContainer.getNewBlockMaterial(), eventContainer.getNewBlockData());
        }
    }

}
