package com.terraboxstudios.instantreplay.replay;

import com.terraboxstudios.instantreplay.inventory.CustomInventory;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ReplayContext {

    private final UUID viewer;
    private final long startTimestamp, timeOfCommand;
    private final int radius;
    private final Location location;
    private final Map<UUID, NPC> npcMap;
    private final Map<UUID, Inventory> npcInventoryMap;
    private final Map<UUID, CustomInventory> npcCustomInventoryMap;
    @Setter
    private int speed;

    private ReplayContext(ReplayContext.Builder builder) {
        this.viewer = builder.viewer;
        this.startTimestamp = builder.startTimestamp;
        this.timeOfCommand = builder.timeOfCommand;
        this.radius = builder.radius;
        this.location = builder.location;
        this.speed = builder.speed;
        this.npcMap = new HashMap<>();
        this.npcInventoryMap = new HashMap<>();
        this.npcCustomInventoryMap = new HashMap<>();
    }

    @RequiredArgsConstructor
    public static class Builder {

        private final UUID viewer;
        private final long startTimestamp, timeOfCommand;
        private final int radius;
        private final Location location;
        private int speed = 1;

        public Builder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public ReplayContext build() {
            return new ReplayContext(this);
        }

    }

}
