package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.replay.ReplayContext;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class EventContainerRenderer<T extends EventContainer> {

    private final List<T> eventContainers;
    private final ReplayContext context;

    public EventContainerRenderer(ReplayContext context, EventContainerProvider<T> eventContainerProvider) {
        this.context = context;
        this.eventContainers = eventContainerProvider
                .getEventContainers(context.getLocation(), context.getRadius(), context.getSpeed())
                .stream()
                .peek(container -> container.setTime(
                        Math.round(container.getTime() / (double) 100) * 100L
                ))
                .collect(Collectors.toList());
    }

    public void render(long currentTimestamp) {
        if (eventContainers.size() < 1) return;
        boolean hasRendered = false;
        for (T container : eventContainers) {
            if (container.getTime() == currentTimestamp) {
                render(container);
                hasRendered = true;
            } else if (hasRendered) {
                break;
            }
        }
    }

    protected abstract void render(T eventContainer);

}
