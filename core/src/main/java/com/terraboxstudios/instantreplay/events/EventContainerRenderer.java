package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.services.EventContainerProviderService;
import lombok.Getter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

@Getter
public abstract class EventContainerRenderer<T extends EventContainer> {

    private final ConcurrentLinkedQueue<T> eventContainers;
    private final ReplayContext context;
    private final EventContainerProvider<T> eventContainerProvider;
    private final Semaphore providerLock;

    public EventContainerRenderer(ReplayContext context, EventContainerProvider<T> eventContainerProvider) {
        this.context = context;
        this.eventContainerProvider = eventContainerProvider;
        this.eventContainers = new ConcurrentLinkedQueue<>();
        this.eventContainers.addAll(eventContainerProvider.provide(context, true));
        this.providerLock = new Semaphore(1);
    }

    public void render(long currentTimestamp) {
        boolean hasRendered = false;
        for (int i = 0; i < eventContainers.size(); i++) {
            T container = eventContainers.peek();
            if (container.getTime() <= currentTimestamp) {
                render(container);
                eventContainers.poll();
                hasRendered = true;
            } else if (hasRendered) {
                break;
            }
            if (eventContainers.size() <= MySQL.getInstance().getEventRenderBuffer()
                    && !eventContainers.isEmpty()
                    && getProviderLock().tryAcquire()) {
                T lastEventContainer = null;
                for (T eventContainer : eventContainers) {
                    lastEventContainer = eventContainer;
                }
                if (lastEventContainer != null) {
                    provideNextContainers(lastEventContainer);
                }
            }
        }
    }

    private void provideNextContainers(T lastEventContainer) {
        EventContainerProviderService.getInstance().provide(this, lastEventContainer);
    }

    protected abstract void render(T eventContainer);

}
