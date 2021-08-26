package com.terraboxstudios.instantreplay.verionspecific.v1_8_R1.npc;

import com.mojang.authlib.GameProfile;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NPCImpl extends NPC {

    private final EntityPlayer entityPlayer;

    public NPCImpl(UUID viewer, UUID uniqueId, String name, NPCSkin<?> skin, World world) {
        super(viewer, uniqueId, name, skin, world);
        if (!(skin.getSkin() instanceof GameProfile)) {
            throw new IllegalArgumentException("NPCSkin object must be of type GameProfile in version specific implementation.");
        }
        GameProfile gameProfile = (GameProfile) skin.getSkin();
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        entityPlayer.setCustomName(name);
        entityPlayer.setCustomNameVisible(true);
    }

    @Override
    public boolean isInvisible() {
        return entityPlayer.isInvisible();
    }

    @Override
    public void setInvisible(boolean invisible) {
        entityPlayer.setInvisible(invisible);
    }

    @Override
    public void setItemInMainHand(ItemStack itemInHand) {

    }

    @Override
    public void setItemInOffHand(ItemStack itemInHand) {

    }

    @Override
    public void specificSpawn(Location location) {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (entityPlayer.yaw * 256 / 360)));
    }

    @Override
    public void specificDeSpawn() {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
    }

    @Override
    public void setEquipmentSlot(int i, ItemStack item) {

    }

    @Override
    public void moveTo(Location location) {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        Location oldLocation = new Location(location.getWorld(), entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, entityPlayer.yaw, entityPlayer.pitch);
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;
        if (oldLocation.distance(location) >= 8) {
            connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
        } else {
            connection.sendPacket(new PacketPlayOutRelEntityMove(
                    entityPlayer.getId(),
                    (byte) (location.getX() * 4096),
                    (byte) (location.getY() * 4096),
                    (byte) (location.getZ() * 4096),
                    true
            ));
        }
        if (oldLocation.getYaw() != location.getYaw() || oldLocation.getPitch() != location.getPitch()) {
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (location.getYaw() * 256 / 360)));
            connection.sendPacket(new PacketPlayOutEntityLook(
                    entityPlayer.getId(),
                    (byte) (location.getYaw() * 256 / 360),
                    (byte) (location.getPitch() * 256 / 360),
                    true
            ));
        }
    }

}
