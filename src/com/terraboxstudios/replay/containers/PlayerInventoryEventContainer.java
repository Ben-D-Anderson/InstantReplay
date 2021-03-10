package com.terraboxstudios.replay.containers;

import com.terraboxstudios.replay.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInventoryEventContainer implements Runnable {

	private final String name, world, serializedInventory;
	private final UUID uuid;
	private final ItemStack[] contents, armourContents, health;
	private final int x, y, z, heldSlot;
	private long time;

	public PlayerInventoryEventContainer(String name, UUID uuid, String serializedInventory, ItemStack[] contents,
			ItemStack[] armourContents, ItemStack[] health, int heldSlot, String world, int x, int y, int z, long time) {
		this.armourContents = armourContents;
		this.contents = contents;
		this.health = health;
		this.uuid = uuid;
		this.name = name;
		this.world = world;
		this.serializedInventory = serializedInventory;
		this.heldSlot = heldSlot;
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
	}

	public int getHeldSlot() {
		return heldSlot;
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public String getSerializedInventory() {
		return serializedInventory;
	}

	public UUID getUuid() {
		return uuid;
	}

	public ItemStack[] getHealth() {
		return health;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public ItemStack[] getArmourContents() {
		return armourContents;
	}

	@Override
	public void run() {
		MySQL.logPlayerInventoryEvent(this);
	}

}
