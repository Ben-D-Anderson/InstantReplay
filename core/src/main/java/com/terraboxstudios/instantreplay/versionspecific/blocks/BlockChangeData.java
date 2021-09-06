package com.terraboxstudios.instantreplay.versionspecific.blocks;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class BlockChangeData<T> {

    private final T data;

    public abstract JsonObject toJson();

}
