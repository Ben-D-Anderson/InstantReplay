package com.terraboxstudios.replay.util;

import net.minecraft.util.com.mojang.authlib.properties.Property;

import java.util.HashMap;
import java.util.UUID;

public class SkinCache {

	private static final HashMap<UUID, Property> cachedSkins = new HashMap<>();
	
	public static void addToCachedSkins(UUID uuid, Property property) {
		cachedSkins.put(uuid, property);
	}
	
	public static boolean isSkinCached(UUID uuid) {
		return cachedSkins.containsKey(uuid);
	}
	
	public static Property getCachedSkin(UUID uuid) {
		return cachedSkins.get(uuid);
	}
	
	public static void removeCachedSkin(UUID uuid) {
		cachedSkins.remove(uuid);
	}
	
}
