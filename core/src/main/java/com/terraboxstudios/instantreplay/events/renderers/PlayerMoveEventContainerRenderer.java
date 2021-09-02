package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.InstantReplay;
import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.inventory.CustomInventory;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                npc.moveTo(eventContainer.getLocation());
            }
            CustomInventory customInventory = getContext().getNpcCustomInventoryMap().get(eventContainer.getUuid());
            if (customInventory != null) {
                ItemStack item = customInventory.getHands()[0];
                if (item == null)
                    item = new ItemStack(Material.AIR);
                npc.setItemInMainHand(item);
                item = customInventory.getHands()[1];
                if (item == null) {
                    item = new ItemStack(Material.AIR);
                }
                npc.setItemInOffHand(item);
                npc.setEquipmentSlot(0, item);
                for (int armourSlot = 0; armourSlot < 4; armourSlot++) {
                    item = customInventory.getArmourContents()[armourSlot];
                    if (item == null)
                        item = new ItemStack(Material.AIR);
                    npc.setEquipmentSlot(armourSlot + 1, item);
                }

            }
        } else {
            NPCSkin<?> skin = InstantReplay.getVersionSpecificProvider().getNPCFactory()
                    .getSkin(eventContainer.getUuid(), eventContainer.getName());
            npc = InstantReplay.getVersionSpecificProvider().getNPCFactory().createNPC(
                    player.getUniqueId(),
                    eventContainer.getUuid(),
                    eventContainer.getName(),
                    skin,
                    eventContainer.getWorld()
            );
            npc.spawn(eventContainer.getLocation());
            getContext().getNpcMap().put(eventContainer.getUuid(), npc);
        }
    }

}
