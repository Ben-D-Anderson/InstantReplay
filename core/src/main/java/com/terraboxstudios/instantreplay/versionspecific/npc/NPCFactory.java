package com.terraboxstudios.instantreplay.versionspecific.npc;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class NPCFactory {

    private final Map<UUID, NPCSkin<?>> skinCache;

    public NPCFactory() {
        skinCache = new HashMap<>();
    }

    private boolean isSkinCached(UUID uuid) {
        return skinCache.containsKey(uuid);
    }

    protected abstract NPCSkin<?> createSkin(UUID uuid, String name);

    public final NPCSkin<?> getSkin(UUID uniqueId, String name) {
        if (!isSkinCached(uniqueId)) {
            skinCache.put(uniqueId, createSkin(uniqueId, name));
        }
        return skinCache.get(uniqueId);
    }

    public abstract NPC createNPC(UUID viewer, UUID uniqueId, String name, NPCSkin<?> skin, World world);

}
