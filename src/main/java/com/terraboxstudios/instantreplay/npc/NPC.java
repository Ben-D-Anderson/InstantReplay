package com.terraboxstudios.instantreplay.npc;

import org.bukkit.inventory.ItemStack;

public abstract class NPC {

    public abstract void setInvisible(boolean invisible);

    public abstract boolean isInvisible();

    public abstract void setItemInHand(ItemStack itemInHand);

    public abstract void spawn();

    public abstract void deSpawn();

    public abstract void setEquipmentSlot(int i, ItemStack item);

    public abstract void playTeleportPacket();

    public abstract void playerHeadPacket();

    public abstract void setLocation(double x, double y, double z, float yaw, float pitch);

}
