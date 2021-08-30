package com.terraboxstudios.instantreplay.events.containers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
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
