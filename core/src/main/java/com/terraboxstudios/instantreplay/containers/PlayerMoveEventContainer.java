package com.terraboxstudios.instantreplay.containers;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class PlayerMoveEventContainer implements Runnable {

	@Getter
	private final String name, world;
	private final double x, y, z;
	@Getter
	@Setter
	private long time;
	@Getter
	private final UUID uuid;
	private final float yaw, pitch;

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
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

}
