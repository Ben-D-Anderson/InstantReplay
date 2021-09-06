package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerChangeBlockEventContainer;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerChangeBlockEventContainerRenderer extends EventContainerRenderer<PlayerChangeBlockEventContainer> {

    public PlayerChangeBlockEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerChangeBlockEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    public void renderRemaining(ReplayContext context) {
        render(context.getTimeOfCommand());
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

        BlockChange blockChange = undoRender ? eventContainer.getOldBlock() : eventContainer.getNewBlock();
        blockChange.send(player, eventContainer.getLocation());
    }

}
