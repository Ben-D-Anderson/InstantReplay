package com.terraboxstudios.instantreplay.events;

import org.bukkit.Location;

import java.util.List;

public interface EventContainerProvider<T extends EventContainer> {

    List<T> getEventContainers(Location replayLocation, int radius, long timestamp);

}
