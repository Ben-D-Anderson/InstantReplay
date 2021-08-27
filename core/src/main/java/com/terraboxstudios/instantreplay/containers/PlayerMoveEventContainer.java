package com.terraboxstudios.instantreplay.containers;

import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
public final class PlayerMoveEventContainer extends EventContainer {

	private final String name;
	private final float yaw, pitch;

	public PlayerMoveEventContainer(UUID uuid, Location location, long time, String name) {
		super(uuid, location, time);
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
		this.name = name;
	}

	@Override
	public Location getLocation() {
		Location location = super.getLocation();
		location.setYaw(yaw);
		location.setPitch(pitch);
		return location;
	}

}
