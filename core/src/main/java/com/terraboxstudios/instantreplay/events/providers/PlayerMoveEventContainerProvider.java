package com.terraboxstudios.instantreplay.events.providers;

import com.terraboxstudios.instantreplay.events.EventContainer;
import com.terraboxstudios.instantreplay.events.EventContainerProvider;
import com.terraboxstudios.instantreplay.events.containers.PlayerDeathDamageEventContainer;
import com.terraboxstudios.instantreplay.events.containers.PlayerJoinLeaveEventContainer;
import com.terraboxstudios.instantreplay.events.containers.PlayerMoveEventContainer;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
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
    public List<PlayerMoveEventContainer> getEventContainers(ReplayContext context, boolean firstRequest) {
        List<PlayerMoveEventContainer> containers = MySQL.getInstance().getPlayerMoveEvents(context);
        new ArrayList<>(containers).forEach(container -> predictMovementContainers(containers, container));
        if (firstRequest) containers.addAll(calculatePreReplayEvents(context));
        return containers;
    }

    private List<PlayerMoveEventContainer> calculatePreReplayEvents(ReplayContext context) {
        List<PlayerMoveEventContainer> preMoveEvents = MySQL.getInstance().getPreReplayPlayerMoveEvents(context);
        List<PlayerDeathDamageEventContainer> preDeathEvents = MySQL.getInstance().getPreReplayDeathEvents(context);
        List<PlayerJoinLeaveEventContainer> preLeaveEvents = MySQL.getInstance().getPreReplayLeaveEvents(context);
        preDeathEvents.forEach(deathEvent -> preMoveEvents.removeIf(moveEvent -> deathEvent.getUuid().equals(moveEvent.getUuid()) && deathEvent.getTime() >= moveEvent.getTime()));
        preLeaveEvents.forEach(leaveEvent -> preMoveEvents.removeIf(moveEvent -> leaveEvent.getUuid().equals(moveEvent.getUuid()) && leaveEvent.getTime() >= moveEvent.getTime()));
        return preMoveEvents;
    }

    private void predictMovementContainers(List<PlayerMoveEventContainer> containers, PlayerMoveEventContainer current) {
        if (!previousContainers.containsKey(current.getUuid())) {
            previousContainers.put(current.getUuid(), current);
            return;
        }
        PlayerMoveEventContainer previous = previousContainers.get(current.getUuid());
        previousContainers.put(current.getUuid(), current);
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
            long newTimestamp = previous.getTime() + (i * (1000L / containersToPredict + 1));
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
