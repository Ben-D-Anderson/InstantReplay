package com.terraboxstudios.instantreplay.versionspecific.v1_17_R1.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCImpl extends NPC {

    private final EntityPlayer entityPlayer;

    public NPCImpl(UUID viewer, UUID uniqueId, String name, NPCSkin<?> skin, World world) {
        super(viewer, uniqueId, name, skin, world);
        if (skin == null || !(skin.getSkin() instanceof GameProfile)) {
            throw new IllegalArgumentException("NPCSkin object must be of type GameProfile in version specific implementation.");
        }
        GameProfile gameProfile = (GameProfile) skin.getSkin();
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile);
        entityPlayer.setCustomName(new ChatMessage(name));
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
        setEquipmentSlot(0, itemInHand);
    }

    @Override
    public void setItemInOffHand(ItemStack itemInHand) {
        setEquipmentSlot(-1, itemInHand);
    }

    @Override
    public void specificSpawn(Location location) {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().b;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (entityPlayer.getBukkitEntity().getLocation().getYaw() * 256 / 360)));
    }

    @Override
    public void specificDeSpawn() {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().b;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, entityPlayer));
        //connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
    }

    @Override
    public void setEquipmentSlot(int i, ItemStack item) {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().b;
        List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(getItemSlotFromInt(i), nmsItemStack));
        connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), pairList));
    }

    private EnumItemSlot getItemSlotFromInt(int i) {
        switch (i) {
            case -1:
                return EnumItemSlot.b;
            case 0:
                return EnumItemSlot.a;
            case 1:
                return EnumItemSlot.c;
            case 2:
                return EnumItemSlot.d;
            case 3:
                return EnumItemSlot.e;
            case 4:
                return EnumItemSlot.f;
            default:
                return null;
        }
    }

    @Override
    public void moveTo(Location location) {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        Location oldLocation = new Location(location.getWorld(), entityPlayer.locX(), entityPlayer.locY(),
                entityPlayer.locZ(), entityPlayer.getBukkitEntity().getLocation().getYaw(), entityPlayer.getBukkitEntity().getLocation().getPitch());
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
        if (oldLocation.getYaw() != location.getYaw() || oldLocation.getPitch() != location.getPitch()) {
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (location.getYaw() * 256 / 360)));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(
                    entityPlayer.getId(),
                    (byte) (location.getYaw() * 256 / 360),
                    (byte) (location.getPitch() * 256 / 360),
                    true
            ));
        }
    }

    @Override
    public int getId() {
        return entityPlayer.getId();
    }

    @Override
    public Location getLocation() {
        return entityPlayer.getBukkitEntity().getLocation();
    }

}
