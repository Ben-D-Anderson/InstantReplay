package com.terraboxstudios.instantreplay.containers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class BlockEventContainer extends EventContainer {

	private final Material oldBlockMaterial, newBlockMaterial;
	private final byte oldBlockData, newBlockData;

	public BlockEventContainer(UUID uuid, Location location, long time, Material oldBlockMaterial, Material newBlockMaterial, byte oldBlockData, byte newBlockData) {
		super(uuid, location, time);
		this.oldBlockMaterial = oldBlockMaterial;
		this.newBlockMaterial = newBlockMaterial;
		this.oldBlockData = oldBlockData;
		this.newBlockData = newBlockData;
	}

}
