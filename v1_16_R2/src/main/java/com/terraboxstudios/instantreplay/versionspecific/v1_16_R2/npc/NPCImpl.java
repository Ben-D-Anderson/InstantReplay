package com.terraboxstudios.instantreplay.versionspecific.v1_16_R2.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NPCImpl extends NPC {

    private final EntityPlayer entityPlayer;
    private final Map<Integer, ItemStack> equipmentCache;

    public NPCImpl(UUID viewer, UUID uniqueId, String name, NPCSkin<?> skin, World world) {
        super(viewer, uniqueId, name, skin, world);
        if (skin == null || !(skin.getSkin() instanceof GameProfile)) {
            throw new IllegalArgumentException("NPCSkin object must be of type GameProfile in version specific implementation.");
        }
        GameProfile gameProfile = (GameProfile) skin.getSkin();
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        entityPlayer.setCustomName(new ChatMessage(name));
        entityPlayer.setCustomNameVisible(true);
        equipmentCache = new HashMap<>();
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
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
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
        if (equipmentCache.containsKey(i)
                && equipmentCache.get(i) != null
                && equipmentCache.get(i).isSimilar(item)) return;
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        net.minecraft.server.v1_16_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R2.ItemStack>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(getItemSlotFromInt(i), nmsItemStack));
        connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), pairList));
        equipmentCache.put(i, item);
    }

    private EnumItemSlot getItemSlotFromInt(int i) {
        switch (i) {
            case -1:
                return EnumItemSlot.OFFHAND;
            case 0:
                return EnumItemSlot.MAINHAND;
            case 1:
                return EnumItemSlot.FEET;
            case 2:
                return EnumItemSlot.LEGS;
            case 3:
                return EnumItemSlot.CHEST;
            case 4:
                return EnumItemSlot.HEAD;
            default:
                return null;
        }
    }

    @Override
    public void moveTo(Location location) {
        Player viewer = Bukkit.getPlayer(getViewer());
        if (viewer == null) return;
        Location oldLocation = new Location(location.getWorld(), entityPlayer.locX(), entityPlayer.locY(), entityPlayer.locZ(), entityPlayer.yaw, entityPlayer.pitch);
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;
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
