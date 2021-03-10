package com.terraboxstudios.replay.containers;

import com.terraboxstudios.replay.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class DeathDamageEventContainer implements Runnable {

	private final String world, name, type, source;
	private long time;
	private final UUID uuid;
	private final int x, y, z;

	public DeathDamageEventContainer(String type, String source, String name, UUID uuid, String world, int x, int y, int z, long time) {
		this.type = type;
		this.source = source;
		this.name = name;
		this.uuid = uuid;
		this.time = time;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
	@Override
	public void run() {
		MySQL.logDeathDamageEvent(this);
	}

	public String getName() {
		return name;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getType() {
		return type;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public String getSource() {
		return source;
	}

}
