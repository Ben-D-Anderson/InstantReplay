package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.inventory.CustomInventory;
import com.terraboxstudios.instantreplay.inventory.InventoryFactory;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInventoryEventContainerRenderer extends EventContainerRenderer<PlayerInventoryEventContainer> {

    public PlayerInventoryEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerInventoryEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    @Override
    protected void render(PlayerInventoryEventContainer eventContainer) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;

        CustomInventory previousInventory = getContext().getNpcCustomInventoryMap().get(eventContainer.getUuid());
        CustomInventory newCustomInventory = new CustomInventory(eventContainer.getContents(), eventContainer.getArmourContents(), eventContainer.getHands(), eventContainer.getHealth());
        if (previousInventory != null && previousInventory.equals(newCustomInventory)) return;

        renderInvToNPC(eventContainer.getUuid(), newCustomInventory);

        getContext().getNpcCustomInventoryMap().put(eventContainer.getUuid(), newCustomInventory);
        Inventory npcInv = getContext().getNpcInventoryMap().get(eventContainer.getUuid());
        if (npcInv == null) {
            getContext().getNpcInventoryMap().put(eventContainer.getUuid(),
                    InventoryFactory.getInstance().createNPCInventory(
                            newCustomInventory,
                            eventContainer.getName()
                    )
            );
        } else {
            InventoryFactory.getInstance().updateNPCInventory(
                    npcInv,
                    newCustomInventory
            );
        }
    }

    private void renderInvToNPC(UUID uuid, CustomInventory customInventory) {
        NPC npc = getContext().getNpcMap().get(uuid);
        if (npc != null) {
            ItemStack item = customInventory.getHands()[0];
            if (item == null)
                item = new ItemStack(Material.AIR);
            npc.setItemInMainHand(item);
            item = customInventory.getHands()[1];
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            npc.setItemInOffHand(item);
            for (int armourSlot = 0; armourSlot < 4; armourSlot++) {
                item = customInventory.getArmourContents()[armourSlot];
                if (item == null)
                    item = new ItemStack(Material.AIR);
                npc.setEquipmentSlot(armourSlot + 1, item);
            }
        }
    }

}
