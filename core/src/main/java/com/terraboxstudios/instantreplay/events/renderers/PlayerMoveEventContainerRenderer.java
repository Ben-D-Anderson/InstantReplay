package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.InstantReplay;
import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerMoveEventContainerRenderer extends EventContainerRenderer<PlayerMoveEventContainer> {

    public PlayerMoveEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerMoveEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    @Override
    protected void render(PlayerMoveEventContainer eventContainer) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;
        NPC npc = getContext().getNpcMap().get(eventContainer.getUuid());
        if (npc != null) {
            if (!Utils.isLocationInReplay(eventContainer.getLocation(), player.getLocation(), getContext().getRadius())) {
                if (npc.isSpawned() && !npc.isInvisible()) {
                    npc.setInvisible(true);
                    npc.deSpawn();
                }
                return;
            }

            if (npc.isInvisible()) {
                npc.setInvisible(false);
            }
            if (!npc.isSpawned()) {
                npc.spawn(eventContainer.getLocation());
            } else {
                if (!eventContainer.getLocation().equals(npc.getLocation())) {
                    npc.moveTo(eventContainer.getLocation());
                }
            }
        } else {
            NPCSkin<?> skin = InstantReplay.getVersionSpecificProvider().getNpcFactory()
                    .getSkin(eventContainer.getUuid(), eventContainer.getName());
            npc = InstantReplay.getVersionSpecificProvider().getNpcFactory().createNPC(
                    player.getUniqueId(),
                    eventContainer.getUuid(),
                    eventContainer.getName(),
                    skin,
                    Bukkit.getWorld(eventContainer.getWorld())
            );
            npc.spawn(eventContainer.getLocation());
            getContext().getNpcMap().put(eventContainer.getUuid(), npc);
        }
    }

}
