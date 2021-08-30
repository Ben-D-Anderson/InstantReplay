package com.terraboxstudios.instantreplay.services;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;

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

	public void logEvent(EventContainer eventContainer) {
		service.execute(() -> MySQL.getInstance().logEvent(eventContainer));
	}

}
