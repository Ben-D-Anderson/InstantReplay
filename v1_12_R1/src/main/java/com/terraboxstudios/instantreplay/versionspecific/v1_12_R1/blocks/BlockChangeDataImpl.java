package com.terraboxstudios.instantreplay.versionspecific.v1_12_R1.blocks;

import com.google.gson.JsonObject;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeData;

public class BlockChangeDataImpl extends BlockChangeData<BlockChangeDataRaw> {

    public BlockChangeDataImpl(BlockChangeDataRaw data) {
        super(data);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("material", getData().getMaterial().toString());
        jsonObject.addProperty("data", getData().getData());
        return jsonObject;
    }

}
