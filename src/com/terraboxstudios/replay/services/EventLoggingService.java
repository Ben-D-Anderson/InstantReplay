package com.terraboxstudios.replay.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoggingService {

	private final ExecutorService service;
	private static EventLoggingService instance;
	
	private EventLoggingService() {
		service = Executors.newSingleThreadExecutor();
	}
	
	public void shutdown() {
		service.shutdown();
	}
	
	public static EventLoggingService getInstance() {
		if (instance == null) instance = new EventLoggingService();
		return instance;
	}

	public void logEvent(Runnable eventContainer) {
		service.execute(eventContainer);
	}

}
