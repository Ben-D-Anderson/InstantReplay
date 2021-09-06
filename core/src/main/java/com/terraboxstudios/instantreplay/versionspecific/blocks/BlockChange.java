package com.terraboxstudios.instantreplay.versionspecific.blocks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public abstract class BlockChange {

    private final BlockChangeData<?> blockChangeData;

    public abstract void send(Player player, Location location);

}
