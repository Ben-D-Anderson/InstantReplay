package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.services.EventContainerProviderService;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
        this.eventContainers.addAll(eventContainerProvider.provide(context));
        this.providerLock = new Semaphore(1);
    }

    public void render(long currentTimestamp) {
        boolean hasRendered = false;
        while (!eventContainers.isEmpty()) {
            T container = eventContainers.peek();
            if (container.getTime() <= currentTimestamp) {
                render(container);
                eventContainers.poll();
                hasRendered = true;
            } else if (hasRendered) {
                break;
            }
            while (eventContainers.isEmpty() && getProviderLock().availablePermits() == 0) {
                //wait for new containers to be provided
                Bukkit.getLogger().log(Level.WARNING, "Replay events aren't being provided fast enough. Is the server or database running behind?");
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException ignored) {
                }
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
