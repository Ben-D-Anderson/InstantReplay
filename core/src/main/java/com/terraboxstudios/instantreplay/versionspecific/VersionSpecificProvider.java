package com.terraboxstudios.instantreplay.versionspecific;

import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCFactory;

public interface VersionSpecificProvider {

    NPCFactory getNPCFactory();
    ItemFactory getItemFactory();

}
