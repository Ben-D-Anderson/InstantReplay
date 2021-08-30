package com.terraboxstudios.instantreplay.events.containers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class PlayerChangeBlockEventContainer extends EventContainer {

	//todo version specific implementation
	private final Material oldBlockMaterial, newBlockMaterial;
	private final byte oldBlockData, newBlockData;

	public PlayerChangeBlockEventContainer(UUID uuid, Location location, long time, Material oldBlockMaterial, Material newBlockMaterial, byte oldBlockData, byte newBlockData) {
		super(uuid, location, time);
		this.oldBlockMaterial = oldBlockMaterial;
		this.newBlockMaterial = newBlockMaterial;
		this.oldBlockData = oldBlockData;
		this.newBlockData = newBlockData;
	}

}
