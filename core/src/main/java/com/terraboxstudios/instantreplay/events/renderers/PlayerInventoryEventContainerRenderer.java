package com.terraboxstudios.instantreplay.events.renderers;

import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.events.containers.PlayerInventoryEventContainer;
import com.terraboxstudios.instantreplay.inventory.CustomInventory;
import com.terraboxstudios.instantreplay.inventory.InventoryFactory;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PlayerInventoryEventContainerRenderer extends EventContainerRenderer<PlayerInventoryEventContainer> {

    public PlayerInventoryEventContainerRenderer(ReplayContext context, EventContainerProvider<PlayerInventoryEventContainer> eventContainerProvider) {
        super(context, eventContainerProvider);
    }

    @Override
    protected void render(PlayerInventoryEventContainer eventContainer) {
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) return;

        CustomInventory previousInventory = getContext().getNpcCustomInventoryMap().get(eventContainer.getUuid());
        CustomInventory newCustomInventory = new CustomInventory(eventContainer.getContents(), eventContainer.getArmourContents(), eventContainer.getHealth(), eventContainer.getHeldSlot());
        if (previousInventory != null && previousInventory.equals(newCustomInventory)) return;

        getContext().getNpcCustomInventoryMap().put(eventContainer.getUuid(), newCustomInventory);
        Inventory npcInv = getContext().getNpcInventoryMap().get(eventContainer.getUuid());
        if (npcInv == null) {
            getContext().getNpcInventoryMap().put(eventContainer.getUuid(),
                    InventoryFactory.getInstance().createNPCInventory(
                            getContext().getNpcCustomInventoryMap().get(eventContainer.getUuid()),
                            eventContainer.getName()
                    )
            );
        } else {
            InventoryFactory.getInstance().updateNPCInventory(
                    npcInv,
                    getContext().getNpcCustomInventoryMap().get(eventContainer.getUuid())
            );
        }
    }

}
