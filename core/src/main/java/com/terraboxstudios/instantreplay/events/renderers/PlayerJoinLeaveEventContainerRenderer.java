package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerJoinLeaveEventContainer;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerJoinLeaveEventContainerRenderer extends EventContainerRenderer<PlayerJoinLeaveEventContainer> {

    public PlayerJoinLeaveEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerJoinLeaveEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    @Override
    protected void render(PlayerJoinLeaveEventContainer eventContainer) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;

        if (eventContainer.getType().equalsIgnoreCase("JOIN")) {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("player-join").replace("{PLAYER}", eventContainer.getName()));
        } else if (eventContainer.getType().equalsIgnoreCase("LEAVE")) {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("player-leave").replace("{PLAYER}", eventContainer.getName()));

            NPC npc = getContext().getNpcMap().get(eventContainer.getUuid());
            if (npc == null) return;

            npc.setInvisible(true);
            npc.deSpawn();
        } else {
            throw new IllegalArgumentException("JoinLeaveEvent has invalid type field");
        }
    }

}
