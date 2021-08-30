package com.terraboxstudios.instantreplay.events.containers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class PlayerInventoryEventContainer extends EventContainer {

    private final String name, serializedInventory;
    private final ItemStack[] contents, armourContents, health;
    private final int heldSlot;

    public PlayerInventoryEventContainer(UUID uuid, Location location, long time,
                                         String name, String serializedInventory,
                                         ItemStack[] contents, ItemStack[] armourContents,
                                         ItemStack[] health, int heldSlot) {
        super(uuid, location, time);
        this.name = name;
        this.serializedInventory = serializedInventory;
        this.contents = contents;
        this.armourContents = armourContents;
        this.health = health;
        this.heldSlot = heldSlot;
    }

}
