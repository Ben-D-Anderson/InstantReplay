package com.terraboxstudios.instantreplay.npc.nms;

import com.terraboxstudios.instantreplay.npc.NPC;
import com.terraboxstudios.instantreplay.npc.NPCFactory;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NPC_1_7_R4 extends NPC {

    private final EntityPlayer npc;
    private final Player viewer;

    public NPC_1_7_R4(Player viewer, String npcName, UUID npcUUID) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) viewer.getLocation().getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), npcName);
        NPCFactory.getInstance().setSkin(gameProfile, npcUUID);
        this.npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        this.viewer = viewer;
    }

    @Override
    public void setInvisible(boolean invisible) {
        npc.setInvisible(invisible);
    }

    @Override
    public boolean isInvisible() {
        return npc.isInvisible();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        npc.inventory.setCarried(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public void spawn() {
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
    }

    @Override
    public void deSpawn() {
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
    }

    @Override
    public void setEquipmentSlot(int i, ItemStack item) {
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), i, CraftItemStack.asNMSCopy(item)));
    }

    @Override
    public void playTeleportPacket() {
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(npc));
    }

    @Override
    public void playerHeadPacket() {
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
    }

    @Override
    public void setLocation(double x, double y, double z, float yaw, float pitch) {
        npc.setLocation(x, y, z, yaw, pitch);
    }

}
