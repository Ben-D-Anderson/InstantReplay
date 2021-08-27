package com.terraboxstudios.instantreplay.containers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class JoinLeaveEventContainer extends EventContainer {

    private final String name, type;

    public JoinLeaveEventContainer(UUID uuid, Location location, long time, String name, String type) {
        super(uuid, location, time);
        this.name = name;
        this.type = type;
    }


}
