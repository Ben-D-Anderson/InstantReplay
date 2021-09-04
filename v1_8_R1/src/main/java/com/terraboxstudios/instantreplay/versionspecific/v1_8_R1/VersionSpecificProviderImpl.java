package com.terraboxstudios.instantreplay.versionspecific.v1_8_R1;

import com.terraboxstudios.instantreplay.versionspecific.VersionSpecificProvider;
import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCFactory;
import com.terraboxstudios.instantreplay.versionspecific.v1_8_R1.item.ItemFactoryImpl;
import com.terraboxstudios.instantreplay.versionspecific.v1_8_R1.npc.NPCFactoryImpl;

public class VersionSpecificProviderImpl implements VersionSpecificProvider {

    private final NPCFactory npcFactory;
    private final ItemFactory itemFactory;

    public VersionSpecificProviderImpl() {
        npcFactory = new NPCFactoryImpl();
        itemFactory = new ItemFactoryImpl();
    }

    @Override
    public NPCFactory getNPCFactory() {
        return npcFactory;
    }

    @Override
    public ItemFactory getItemFactory() {
        return itemFactory;
    }

}
