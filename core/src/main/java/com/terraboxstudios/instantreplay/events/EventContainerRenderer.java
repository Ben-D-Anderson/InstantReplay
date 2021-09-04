package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.services.EventContainerProviderService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

@Getter
public abstract class EventContainerRenderer<T extends EventContainer> {

    private final List<T> eventContainers;
    private final ReplayContext context;
    private final EventContainerProvider<T> eventContainerProvider;
    private final Semaphore providerLock;

    public EventContainerRenderer(ReplayContext context, EventContainerProvider<T> eventContainerProvider) {
        this.context = context;
        this.eventContainerProvider = eventContainerProvider;
        this.eventContainers = Collections.synchronizedList(new ArrayList<>());
        this.eventContainers.addAll(eventContainerProvider.provide(context));
        this.providerLock = new Semaphore(1);
    }

    public void render(long currentTimestamp) {
        boolean hasRendered = false;
        ListIterator<T> iterator = eventContainers.listIterator();
        while (iterator.hasNext()) {
            T container = iterator.next();
            if (container.getTime() <= currentTimestamp) {
                render(container);
                iterator.remove();
                hasRendered = true;
            } else if (hasRendered) {
                break;
            }
            if (eventContainers.size() <= MySQL.getInstance().getEventRenderBuffer()
                    && eventContainers.size() > 0
                    && getProviderLock().tryAcquire())
                provideNextContainers(eventContainers.get(eventContainers.size() - 1));
        }
    }

    private void provideNextContainers(T lastEventContainer) {
        EventContainerProviderService.getInstance().provide(this, lastEventContainer);
    }

    protected abstract void render(T eventContainer);

}
