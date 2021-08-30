package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerDeathDamageEventContainer;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerDeathDamageEventContainerRenderer extends EventContainerRenderer<PlayerDeathDamageEventContainer> {

    public PlayerDeathDamageEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerDeathDamageEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    @Override
    protected void render(PlayerDeathDamageEventContainer eventContainer) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;

        if (eventContainer.getType().equalsIgnoreCase("DAMAGE")) {
            sendMessage(player, "player-damage", eventContainer);
        } else if (eventContainer.getType().equalsIgnoreCase("DEATH")) {
            sendMessage(player, "player-death", eventContainer);

            NPC npc = getContext().getNpcMap().get(eventContainer.getUuid());
            if (npc == null) return;

            npc.setInvisible(true);
            npc.deSpawn();
        } else {
            throw new IllegalArgumentException("DeathDamageEvent has invalid type field");
        }
    }

    private void sendMessage(Player player, String path, PlayerDeathDamageEventContainer eventContainer) {
        player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString(path)
                .replace("{PLAYER}", eventContainer.getName())
                .replace("{SOURCE}", eventContainer.getSource())
        );
    }

}
