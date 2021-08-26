package com.terraboxstudios.instantreplay.verionspecific.v1_8_R1;

import com.terraboxstudios.instantreplay.verionspecific.v1_8_R1.npc.NPCFactoryImpl;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCFactory;
import com.terraboxstudios.instantreplay.versionspecific.VersionSpecificProvider;

public class VersionSpecificProviderImpl implements VersionSpecificProvider {

    private final NPCFactory npcFactory;

    public VersionSpecificProviderImpl() {
        npcFactory = new NPCFactoryImpl();
    }

    @Override
    public NPCFactory getNPCFactory() {
        return npcFactory;
    }

}
