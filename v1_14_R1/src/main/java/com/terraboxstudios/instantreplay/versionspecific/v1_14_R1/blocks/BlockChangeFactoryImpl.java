package com.terraboxstudios.instantreplay.versionspecific.v1_14_R1.blocks;

import com.google.gson.JsonObject;
import com.terraboxstudios.instantreplay.exceptions.BlockChangeParseException;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeData;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockChangeFactoryImpl implements BlockChangeFactory {

    @Override
    public BlockChange createBlockChange(BlockChangeData<?> blockChangeData) {
        return new BlockChangeImpl(blockChangeData);
    }

    @Override
    public BlockChangeData<?> createBlockChangeData(Block block) {
        return new BlockChangeDataImpl(new BlockChangeDataRaw(block.getBlockData()));
    }

    @Override
    public BlockChangeData<?> createBlockChangeData(JsonObject jsonObject) throws BlockChangeParseException {
        Material material = Material.getMaterial(jsonObject.get("material").getAsString());
        String data = jsonObject.get("data").getAsString();
        if (material == null || data == null) throw new BlockChangeParseException();
        BlockData blockData = Bukkit.createBlockData(material, data);
        return new BlockChangeDataImpl(new BlockChangeDataRaw(blockData));
    }

    @Override
    public BlockChangeData<?> createEmptyBlockChangeData() {
        return new BlockChangeDataImpl(new BlockChangeDataRaw(Bukkit.createBlockData(Material.AIR)));
    }

}
