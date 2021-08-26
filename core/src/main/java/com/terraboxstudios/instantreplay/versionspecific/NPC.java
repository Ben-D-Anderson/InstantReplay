package com.terraboxstudios.instantreplay.versionspecific;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class NPC {

    private boolean spawned = false;

    public abstract boolean isInvisible();
    public abstract void setInvisible(boolean invisible);
    public abstract void setItemInMainHand(ItemStack itemInHand);
    public abstract void setItemInOffHand(ItemStack itemInHand);
    public abstract UUID getUniqueId();
    public abstract void specificSpawn();
    public abstract void specificDeSpawn();
    public abstract void setEquipmentSlot(int i, ItemStack item);
    public abstract void teleport(double x, double y, double z, float yaw, float pitch);

    public void spawn() {
        spawned = true;
        specificSpawn();
    }

    public void deSpawn() {
        spawned = false;
        specificDeSpawn();
    }

    public boolean isSpawned() {
        return spawned;
    }

}
