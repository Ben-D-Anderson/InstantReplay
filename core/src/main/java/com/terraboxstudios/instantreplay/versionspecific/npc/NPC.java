package com.terraboxstudios.instantreplay.versionspecific.npc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class NPC {

    private boolean spawned = false;
    private final UUID viewer;
    private final UUID uniqueId;
    private final String name;
    private final NPCSkin<?> skin;
    private final World world;

    public abstract boolean isInvisible();
    public abstract void setInvisible(boolean invisible);
    public abstract void setItemInMainHand(ItemStack itemInHand);
    public abstract void setItemInOffHand(ItemStack itemInHand);
    protected abstract void specificSpawn(Location location);
    protected abstract void specificDeSpawn();
    public abstract void setEquipmentSlot(int i, ItemStack item);
    public abstract void moveTo(Location location);
    public abstract int getId();

    public abstract Location getLocation();

    public final void spawn(Location location) {
        spawned = true;
        specificSpawn(location);
    }

    public final void deSpawn() {
        spawned = false;
        specificDeSpawn();
    }

}
