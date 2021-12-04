package com.terraboxstudios.instantreplay.versionspecific.v1_18_R1.blocks;

import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlockChangeImpl extends BlockChange {

    public BlockChangeImpl(BlockChangeData<?> blockChangeData) {
        super(blockChangeData);
    }

    @Override
    public void send(Player player, Location location) {
        if (!(getBlockChangeData().getData() instanceof BlockChangeDataRaw)) {
            throw new IllegalArgumentException("BlockChangeData object value must be of type BlockChangeDataRaw in version specific implementation.");
        }
        BlockChangeDataRaw blockChangeDataRaw = (BlockChangeDataRaw) getBlockChangeData().getData();
        player.sendBlockChange(location, blockChangeDataRaw.getBlockData());
    }

}
