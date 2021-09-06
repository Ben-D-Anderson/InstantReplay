package com.terraboxstudios.instantreplay.versionspecific.blocks;

import com.google.gson.JsonObject;
import com.terraboxstudios.instantreplay.exceptions.BlockChangeParseException;
import org.bukkit.block.Block;

public interface BlockChangeFactory {

    BlockChange createBlockChange(BlockChangeData<?> blockChangeData);

    BlockChangeData<?> createBlockChangeData(Block block);

    BlockChangeData<?> createBlockChangeData(JsonObject jsonObject) throws BlockChangeParseException;

    BlockChangeData<?> createEmptyBlockChangeData();

}
