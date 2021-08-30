package com.terraboxstudios.instantreplay.events.containers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class PlayerDeathDamageEventContainer extends EventContainer {

	private final String name, type, source;

	public PlayerDeathDamageEventContainer(UUID uuid, Location location, long time, String name, String type, String source) {
		super(uuid, location, time);
		this.name = name;
		this.type = type;
		this.source = source;
	}

}
