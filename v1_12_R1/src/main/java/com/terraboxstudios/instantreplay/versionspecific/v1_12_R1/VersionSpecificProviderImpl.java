package com.terraboxstudios.instantreplay.versionspecific.v1_12_R1;

import com.terraboxstudios.instantreplay.versionspecific.VersionSpecificProvider;
import com.terraboxstudios.instantreplay.versionspecific.blocks.BlockChangeFactory;
import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCFactory;
import com.terraboxstudios.instantreplay.versionspecific.util.UtilsHelper;
import com.terraboxstudios.instantreplay.versionspecific.v1_12_R1.blocks.BlockChangeFactoryImpl;
import com.terraboxstudios.instantreplay.versionspecific.v1_12_R1.item.ItemFactoryImpl;
import com.terraboxstudios.instantreplay.versionspecific.v1_12_R1.npc.NPCFactoryImpl;
import com.terraboxstudios.instantreplay.versionspecific.v1_12_R1.util.UtilsHelperImpl;
import lombok.Getter;

@Getter
public class VersionSpecificProviderImpl implements VersionSpecificProvider {

    private final NPCFactory npcFactory;
    private final ItemFactory itemFactory;
    private final UtilsHelper utilsHelper;
    private final BlockChangeFactory blockChangeFactory;

    public VersionSpecificProviderImpl() {
        npcFactory = new NPCFactoryImpl();
        itemFactory = new ItemFactoryImpl();
        utilsHelper = new UtilsHelperImpl();
        blockChangeFactory = new BlockChangeFactoryImpl();
    }

}
