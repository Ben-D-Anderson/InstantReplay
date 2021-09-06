package com.terraboxstudios.instantreplay.versionspecific.v1_8_R1.blocks;

import com.google.gson.JsonObject;
import com.terraboxstudios.instantreplay.exceptions.BlockChangeParseException;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeData;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeFactory;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockChangeFactoryImpl implements BlockChangeFactory {

    @Override
    public BlockChange createBlockChange(BlockChangeData<?> blockChangeData) {
        return new BlockChangeImpl(blockChangeData);
    }

    @Override
    public BlockChangeData<?> createBlockChangeData(Block block) {
        return new BlockChangeDataImpl(new BlockChangeDataRaw(block.getType(), block.getData()));
    }

    @Override
    public BlockChangeData<?> createBlockChangeData(JsonObject jsonObject) throws BlockChangeParseException {
        Material material = Material.getMaterial(jsonObject.get("material").getAsString());
        byte data = jsonObject.get("data").getAsByte();
        if (material == null) throw new BlockChangeParseException();
        return new BlockChangeDataImpl(new BlockChangeDataRaw(material, data));
    }

    @Override
    public BlockChangeData<?> createEmptyBlockChangeData() {
        return new BlockChangeDataImpl(new BlockChangeDataRaw(Material.AIR, (byte) 0));
    }

}
