package com.terraboxstudios.instantreplay.replay;

import com.terraboxstudios.instantreplay.events.EventContainerRenderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
public class ReplayContext {

    private final UUID viewer;
    private final long timeOfCommandRun, timestamp;
    private final int radius;
    private final Location location;
    private final EventContainerRenderer<?>[] renderers;
    @Setter
    private int speed;

    private ReplayContext(ReplayContext.Builder builder) {
        this.viewer = builder.viewer;
        this.timestamp = builder.timestamp;
        this.timeOfCommandRun = builder.timeOfCommandRun;
        this.radius = builder.radius;
        this.location = builder.location;
        this.speed = builder.speed;
        this.renderers = builder.renderers;
    }

    @RequiredArgsConstructor
    public static class Builder {

        private final UUID viewer;
        private final long timestamp, timeOfCommandRun;
        private final int radius;
        private final Location location;
        private int speed;
        private EventContainerRenderer<?>[] renderers;

        public Builder setRenderers(EventContainerRenderer<?>... renderers) {
            this.renderers = renderers;
            return this;
        }

        public Builder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public ReplayContext build() {
            return new ReplayContext(this);
        }

    }

}
