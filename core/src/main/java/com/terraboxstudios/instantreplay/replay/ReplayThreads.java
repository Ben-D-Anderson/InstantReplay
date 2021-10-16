package com.terraboxstudios.instantreplay.replay;

import com.terraboxstudios.instantreplay.util.Utils;

import java.util.HashMap;
import java.util.UUID;

public class ReplayThreads {

	private static final HashMap<UUID, ReplayInstance> threads = new HashMap<>();

	public static ReplayInstance getThread(UUID uuid) {
		return threads.get(uuid);
	}

	public static boolean isUserReplaying(UUID uuid) {
		return threads.get(uuid) != null;
	}

	public static void addToThreads(UUID uuid, ReplayInstance thread) {
		threads.put(uuid, thread);
	}

	public static void stopThread(UUID uuid) {
		Utils.runOnMainThread(() -> threads.remove(uuid).stopReplay());
	}

	public static void stopAllThreads() {
		threads.keySet().forEach(uuid -> threads.remove(uuid).stopReplay());
	}

}
