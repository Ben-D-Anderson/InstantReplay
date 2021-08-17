package com.terraboxstudios.instantreplay.threads;

import com.terraboxstudios.instantreplay.Main;
import org.bukkit.Bukkit;

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
		Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
			try {
				threads.get(uuid).stopReplay();
				threads.remove(uuid);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}				
		});
	}

}
