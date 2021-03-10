package com.terraboxstudios.replay.containers;

import com.terraboxstudios.replay.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class PlayerMoveEventContainer implements Runnable {

	private final String name, world;
	private final double x, y, z;
	private long time;
	private final UUID uuid;
	private final float yaw, pitch;
	
	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public PlayerMoveEventContainer(String name, UUID uuid, String world, double x, double y, double z, float yaw, float pitch, long time) {
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.time = time;
		this.uuid = uuid;
	}

	@Override
	public void run() {
		MySQL.logPlayerMoveEvent(this);
	}

	public UUID getUuid() {
		return uuid;
	}

}
