package com.terraboxstudios.instantreplay.events.containers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class PlayerChangeBlockEventContainer extends EventContainer {

	private final BlockChange newBlock, oldBlock;

	public PlayerChangeBlockEventContainer(UUID uuid, Location location, long time, BlockChange newBlock, BlockChange oldBlock) {
		super(uuid, location, time);
		this.oldBlock = oldBlock;
		this.newBlock = newBlock;
	}

}
