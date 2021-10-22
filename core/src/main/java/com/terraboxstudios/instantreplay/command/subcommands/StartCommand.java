package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.replay.ReplayInstance;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

public class StartCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "start";
    }

    @Override
    public String[] getNeededPermissions() {
        return new String[]{
                "replay-permission"
        };
    }

    @Override
    public void process(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-already-running"));
            return;
        }
        if (args.length != 3 && args.length != 2) {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-start"));
            return;
        }

        int radius, speed;
        long timeStamp;
        try {
            radius = Integer.parseInt(args[0]);
            timeStamp = parseTimeArgument(sender, args[1]);
            try {
                speed = Integer.parseInt(args[2]);
                if (speed < 1)
                    speed = 1;
            } catch (Exception e) {
                speed = 1;
            }
            if (radius < 1) {
                radius = 1;
            }
            if (timeStamp < 1) {
                player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
                return;
            }
        } catch (Exception e) {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start"));
            return;
        }

        sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-loading"));

        ReplayContext context = new ReplayContext.Builder(
                player.getUniqueId(),
                timeStamp,
                Utils.roundTime(Calendar.getInstance().getTimeInMillis()),
                radius,
                player.getLocation()
        ).setSpeed(speed).build();
        new ReplayInstance(context);
    }

    private long parseTimeArgument(CommandSender sender, String argument) {
        long longArgument;
        switch (Character.toLowerCase(argument.charAt(argument.length() - 1))) {
            case 's':
                longArgument = Long.parseLong(argument.substring(0, argument.length() - 1));
                return Calendar.getInstance().getTimeInMillis() - (longArgument * 1000);
            case 'm':
                longArgument = Long.parseLong(argument.substring(0, argument.length() - 1));
                return Calendar.getInstance().getTimeInMillis() - (60 * longArgument * 1000);
            case 'h':
                longArgument = Long.parseLong(argument.substring(0, argument.length() - 1));
                return Calendar.getInstance().getTimeInMillis() - (60 * 60 * longArgument * 1000);
            case 't':
                longArgument = Long.parseLong(argument.substring(0, argument.length() - 1));
                int longArgumentLength = Long.toString(longArgument).length();
                if (longArgumentLength == 10) {
                    longArgument *= 1000;
                } else if (longArgumentLength != 13) {
                    return 0;
                }
                return longArgument;
            default:
                ZoneId zoneId = TimeZone.getDefault().toZoneId();
                Optional<ZonedDateTime> zonedDateTimeOptional = Utils.convertToTimestamp(argument, zoneId);
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("parsing-time-argument").replace("{TIMEZONE}", zoneId.toString()));
                return zonedDateTimeOptional.map(ChronoZonedDateTime::toEpochSecond).orElse(0L);
        }
    }

}
