package com.terraboxstudios.instantreplay.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraboxstudios.instantreplay.InstantReplay;
import com.terraboxstudios.instantreplay.exceptions.BlockChangeParseException;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChange;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeData;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BlockChangeSerializer {

    public static String serialize(BlockChange blockChange) {
        String json = blockChange.getBlockChangeData().toJson().toString();
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(jsonBytes);
    }

    public static BlockChange deserialize(String serializedBlockChange) throws BlockChangeParseException {
        byte[] jsonBytes = Base64.getDecoder().decode(serializedBlockChange);
        String jsonStr = new String(jsonBytes, StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        BlockChangeFactory blockChangeFactory = InstantReplay.getVersionSpecificProvider().getBlockChangeFactory();
        BlockChangeData<?> blockChangeData = blockChangeFactory.createBlockChangeData(jsonObject);
        return blockChangeFactory.createBlockChange(blockChangeData);
    }

}
