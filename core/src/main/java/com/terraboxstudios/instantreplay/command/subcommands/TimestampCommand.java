package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

public class TimestampCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return "timestamp";
    }

    @Override
    public String[] getNeededPermissions() {
        return new String[]{
                "replay-permission"
        };
    }

    @Override
    public void process(CommandSender sender, String... args) {
        long timestamp;
        if (args.length > 0) {
            TimeZone timeZone = TimeZone.getDefault();

            //check if timezone was also specified
            if (args.length > 1) {
                String timeZoneStr = args[2];
                try {
                    timeZone = TimeZone.getTimeZone(timeZoneStr);
                } catch (Exception e) {
                    sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-timezone"));
                    return;
                }
            }

            Optional<ZonedDateTime> zonedDateTimeOptional = Utils.convertToTimestamp(args[0], timeZone.toZoneId());
            if (!zonedDateTimeOptional.isPresent()) {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-convert-timestamp")
                        .replace("{FORMAT_DATETIME}", Objects.requireNonNull(Config.getConfig().getString("settings.timestamp-converter-format-datetime")))
                        .replace("{FORMAT_TIME}", Objects.requireNonNull(Config.getConfig().getString("settings.timestamp-converter-format-time"))));
                return;
            }

            timestamp = zonedDateTimeOptional.get().toEpochSecond();
        } else {
            //if player and in replay then send the timestamp in replay
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                    timestamp = ReplayThreads.getThread(player.getUniqueId()).getRendererManager().getCurrentTimestamp() / 1000;
                    Utils.sendReplayTimestampMessage(player, timestamp);
                    return;
                }
            }

            //just use current timestamp if not parsing
            timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        }

        Utils.sendTimestampMessage(sender, timestamp);
    }

}
