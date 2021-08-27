package com.terraboxstudios.instantreplay.containers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class DeathDamageEventContainer extends EventContainer {

	private final String name, type, source;

	public DeathDamageEventContainer(UUID uuid, Location location, long time, String name, String type, String source) {
		super(uuid, location, time);
		this.name = name;
		this.type = type;
		this.source = source;
	}

}
