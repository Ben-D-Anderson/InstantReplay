package com.terraboxstudios.instantreplay.services;

import com.terraboxstudios.instantreplay.mysql.MySQL;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MySQLCleanupService {

	private static ScheduledExecutorService service;
	
	public static void start() {
		if (service != null) return;
		service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleWithFixedDelay(() -> MySQL.getInstance().cleanTables(), 1, 1, TimeUnit.MINUTES);
	}
	
	public static void shutdown() {
		if (service != null)
			service.shutdown();
	}
	
}
