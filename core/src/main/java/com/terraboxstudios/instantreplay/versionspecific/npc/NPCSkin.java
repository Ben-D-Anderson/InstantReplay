package com.terraboxstudios.instantreplay.versionspecific.npc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class NPCSkin<T> {

    private final T skin;

}
