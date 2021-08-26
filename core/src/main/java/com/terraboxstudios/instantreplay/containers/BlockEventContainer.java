package com.terraboxstudios.instantreplay.containers;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockEventContainer implements Runnable {

	private final Material oldBlockMaterial, newBlockMaterial;
	private final byte oldBlockData, newBlockData;
	private long time;
	private final String world;
	private final int x, y, z;

	public BlockEventContainer(String world, int x, int y, int z, Material oldBlockMaterial, byte oldBlockData, Material newBlockMaterial, byte newBlockData, long time) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.oldBlockMaterial = oldBlockMaterial;
		this.newBlockMaterial = newBlockMaterial;
		this.oldBlockData = oldBlockData;
		this.newBlockData = newBlockData;
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Material getOldBlockMaterial() {
		return oldBlockMaterial;
	}

	public Location getLoc() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public Material getNewBlockMaterial() {
		return newBlockMaterial;
	}

	public byte getNewBlockData() {
		return newBlockData;
	}

	public byte getOldBlockData() {
		return oldBlockData;
	}

	@Override
	public void run() {
		MySQL.logBlockEvent(this);
	}

}
