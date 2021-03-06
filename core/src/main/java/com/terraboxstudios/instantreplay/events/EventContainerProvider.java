package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public interface EventContainerProvider<T extends EventContainer> {

    List<T> getEventContainers(ReplayContext context, boolean firstRequest);

    default List<T> provide(ReplayContext context, boolean firstRequest) {
        return getEventContainers(context, firstRequest)
                .stream()
                .peek(container -> container.setTime(Utils.roundTime(container.getTime())))
                .collect(Collectors.toList());
    }

}
