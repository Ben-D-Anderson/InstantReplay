package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.Location;

import java.util.*;

public class PlayerMoveEventContainerProvider implements EventContainerProvider<PlayerMoveEventContainer> {

    private final Map<UUID, PlayerMoveEventContainer> previousContainers;
    private final int containersToPredict;

    public PlayerMoveEventContainerProvider() {
        previousContainers = new HashMap<>();
        containersToPredict = (int) ((Config.getConfig().getDouble("settings.seconds-per-player-move-log") * 1000) / 100) - 1;
    }

    @Override
    public List<PlayerMoveEventContainer> getEventContainers(Location replayLocation, int radius, long timestamp) {
        List<PlayerMoveEventContainer> containers = MySQL.getInstance().getPlayerMoveEvents(replayLocation, radius, timestamp);
        new ArrayList<>(containers).forEach(container -> predictMovementContainers(containers, container));
        return containers;
    }

    private void predictMovementContainers(List<PlayerMoveEventContainer> containers, PlayerMoveEventContainer current) {
        if (!previousContainers.containsKey(current.getUuid())) {
            previousContainers.put(current.getUuid(), current);
            return;
        }
        PlayerMoveEventContainer previous = previousContainers.get(current.getUuid());
        Location previousLocation = previous.getLocation();
        Location locationChange = current.getLocation().subtract(previousLocation);
        locationChange.setYaw(locationChange.getYaw() - previous.getYaw());
        locationChange.setPitch(locationChange.getPitch() - previous.getPitch());

        double reciprocal = (double) 1 / (containersToPredict + 1);
        //multiply by the reciprocal to divide
        Location locationChangePerPrediction = locationChange.multiply(reciprocal);
        locationChangePerPrediction.setYaw((float) (locationChangePerPrediction.getYaw() * reciprocal));
        locationChangePerPrediction.setPitch((float) (locationChangePerPrediction.getPitch() * reciprocal));

        for (int i = 1; i <= containersToPredict; i++) {
            UUID uuid = previous.getUuid();
            //previousLocation increases by 'change per prediction' each iteration
            Location newLocation = previousLocation.add(locationChangePerPrediction);
            newLocation.setYaw(newLocation.getYaw() + locationChangePerPrediction.getYaw());
            newLocation.setPitch(newLocation.getPitch() + locationChangePerPrediction.getPitch());
            long newTimestamp = previous.getTime() + (i * 100L);
            String name = previous.getName();

            PlayerMoveEventContainer newPlayerMoveEventContainer = new PlayerMoveEventContainer(uuid, newLocation, newTimestamp, name);
            insertContainer(containers, newPlayerMoveEventContainer);
        }
    }

    //inserts container at position sorted by time
    private void insertContainer(List<PlayerMoveEventContainer> containers, PlayerMoveEventContainer container) {
        int index = Collections.binarySearch(containers, container, Comparator.comparing(EventContainer::getTime));
        if (index < 0) {
            index = -index - 1;
        }
        containers.add(index, container);
    }

}
