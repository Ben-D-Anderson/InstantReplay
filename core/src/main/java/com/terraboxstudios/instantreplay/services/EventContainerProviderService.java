package com.terraboxstudios.instantreplay.services;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventContainerProviderService {

	private final ExecutorService service;
	private static EventContainerProviderService instance;

	private EventContainerProviderService() {
		service = Executors.newSingleThreadExecutor();
	}

	public void shutdown() {
		service.shutdown();
	}

	public static EventContainerProviderService getInstance() {
		if (instance == null) instance = new EventContainerProviderService();
		return instance;
	}

	public <T extends EventContainer> void provide(EventContainerRenderer<T> eventContainerRenderer, T lastEventContainer) {
		service.execute(() -> {
			ReplayContext bufferContext = new ReplayContext.Builder(
					lastEventContainer.getUuid(),
					lastEventContainer.getTime() + 1,
					eventContainerRenderer.getContext().getTimeOfCommand(),
					eventContainerRenderer.getContext().getRadius(),
					eventContainerRenderer.getContext().getLocation()
			).build();
			List<T> containers = eventContainerRenderer.getEventContainerProvider().provide(bufferContext);
			eventContainerRenderer.getEventContainers().addAll(containers);
			if (containers.size() >= MySQL.getInstance().getEventRenderBuffer()) {
				eventContainerRenderer.getProviderLock().release();
			}
		});
	}

}
