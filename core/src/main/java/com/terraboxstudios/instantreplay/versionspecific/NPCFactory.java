package com.terraboxstudios.instantreplay.versionspecific;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface NPCFactory {

    boolean isSkinCached(UUID uuid);
    boolean cacheSkin(UUID uuid);
    void applySkinFromCache(NPC npc);
    NPC createNPC(Player viewer, String npcName, UUID npcUUID);

}
