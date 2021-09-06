package com.terraboxstudios.instantreplay.versionspecific;

import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeFactory;
import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCFactory;
import com.terraboxstudios.instantreplay.versionspecific.util.UtilsHelper;

public interface VersionSpecificProvider {

    NPCFactory getNpcFactory();

    ItemFactory getItemFactory();

    UtilsHelper getUtilsHelper();

    BlockChangeFactory getBlockChangeFactory();

}
