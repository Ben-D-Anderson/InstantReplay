package com.terraboxstudios.instantreplay.commands;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.replay.ReplayInstance;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

public class ReplayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-reload-permission")))) {
                sender.sendMessage(Config.readColouredString("no-permission"));
                return true;
            }
            Config.reloadConfig();
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("config-reloaded"));
            return true;
        }

        if (args[0].equalsIgnoreCase("clearlogs")) {
            if (!sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-clearlogs-permission")))) {
                sender.sendMessage(Config.readColouredString("no-permission"));
                return true;
            }
            MySQL.getInstance().clearLogs();
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("logs-cleared"));
            return true;
        }

        if (args[0].equalsIgnoreCase("timestamp")) {
            long timestamp;
            if (args.length > 1) {
                Optional<LocalDateTime> localDateTimeOptional = convertToTimestamp(args[1]);
                if (!localDateTimeOptional.isPresent()) {
                    sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-convert-timestamp")
                            .replace("{FORMAT}", Objects.requireNonNull(Config.getConfig().getString("settings.timestamp-converter-format"))));
                    return true;
                }
                TimeZone timeZone = TimeZone.getDefault();
                if (args.length > 2) {
                    String timeZoneStr = args[2];
                    try {
                        timeZone = TimeZone.getTimeZone(timeZoneStr);
                    } catch (Exception e) {
                        sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-timezone"));
                        return true;
                    }
                }
                timestamp = localDateTimeOptional.get().atZone(timeZone.toZoneId()).toEpochSecond();
                Utils.sendTimestampMessage(sender, timestamp);
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                        timestamp = ReplayThreads.getThread(player.getUniqueId()).getRendererManager().getCurrentTimestamp();
                        Utils.sendReplayTimestampMessage(player, timestamp);
                    } else {
                        timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
                        Utils.sendTimestampMessage(player, timestamp);
                    }
                } else {
                    timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
                    Utils.sendTimestampMessage(sender, timestamp);
                }
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command Only Allowed For Players");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-permission")))) {
            sender.sendMessage(Config.readColouredString("no-permission"));
            return true;
        }


        if (args[0].equalsIgnoreCase("stop")) {
            if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                ReplayThreads.stopThread(player.getUniqueId());
                player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-stopped"));
            } else {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("speed")) {
            if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                if (args.length != 2) {
                    sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-speed"));
                    return true;
                }
                int speed;
                try {
                    speed = Integer.parseInt(args[1]);
                    speed = Math.abs(speed);
                    if (speed > 10) {
                        sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-speed"));
                        return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-speed"));
                    return true;
                }
                ReplayThreads.getThread(player.getUniqueId()).getContext().setSpeed(speed);
                player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-speed-changed").replace("{SPEED}", speed + ""));
            } else {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("pause")) {
            if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                if (ReplayThreads.getThread(player.getUniqueId()).getPlaying().get()) {
                    ReplayThreads.getThread(player.getUniqueId()).pauseReplay();
                    player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-paused"));
                } else {
                    sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-already-paused"));
                }
            } else {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("resume")) {
            if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                if (!ReplayThreads.getThread(player.getUniqueId()).getPlaying().get()) {
                    ReplayThreads.getThread(player.getUniqueId()).resumeReplay();
                    player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-resumed"));
                } else {
                    sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-not-paused"));
                }
            } else {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (ReplayThreads.isUserReplaying(player.getUniqueId())) {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-already-running"));
                return true;
            }
            if (args.length != 4 && args.length != 3) {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-start"));
                return true;
            }
        }


        if (!args[0].equalsIgnoreCase("start")
                && !args[0].equalsIgnoreCase("resume")
                && !args[0].equalsIgnoreCase("pause")
                && !args[0].equalsIgnoreCase("stop")
                && !args[0].equalsIgnoreCase("reload")
                && !args[0].equalsIgnoreCase("clearlogs")
                && !args[0].equalsIgnoreCase("timestamp")) {
            sendHelp(sender);
            return true;
        }

        int radius, speed;
        long timeStamp;
        try {
            radius = Integer.parseInt(args[1]);
            timeStamp = parseTimeArgument(args[2]);
            try {
                speed = Integer.parseInt(args[3]);
                if (speed < 1)
                    speed = 1;
            } catch (Exception e) {
                speed = 1;
            }
            if (radius < 1) {
                radius = 1;
            }
            if (timeStamp < 1) {
                sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start-time"));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-start"));
            return true;
        }

        sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-loading"));

        ReplayContext context = new ReplayContext.Builder(
                player.getUniqueId(),
                timeStamp,
                Utils.roundTime(Calendar.getInstance().getTimeInMillis()),
                radius,
                player.getLocation()
        ).setSpeed(speed).build();
        ReplayInstance replayInstance = new ReplayInstance(context);
        ReplayThreads.addToThreads(player.getUniqueId(), replayInstance);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        for (String line : Config.readColouredStringListAsList("invalid-argument")) {
            if (line.startsWith("{") && line.endsWith("]")) {
                String permissiveLine = line.substring(1, line.lastIndexOf("}"));
                String varPermission = line.substring(line.lastIndexOf("}") + 2, line.length() - 1);
                if (sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings." + varPermission)))) {
                    sender.sendMessage(permissiveLine);
                }
            } else {
                sender.sendMessage(line);
            }
        }
    }

    private Optional<LocalDateTime> convertToTimestamp(String argument) {
        String pattern = Objects.requireNonNull(Config.getConfig().getString("settings.timestamp-converter-format"));
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(argument, DateTimeFormatter.ofPattern(pattern));
            return Optional.of(localDateTime);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private long parseTimeArgument(String argument) {
        long timestamp = 0;
        long longArgument = Long.parseLong(argument.substring(0, argument.length() - 1));
        switch (Character.toLowerCase(argument.charAt(argument.length() - 1))) {
            case 's':
                timestamp = Calendar.getInstance().getTimeInMillis() - (longArgument * 1000);
                break;
            case 'm':
                timestamp = Calendar.getInstance().getTimeInMillis() - (60 * longArgument * 1000);
                break;
            case 'h':
                timestamp = Calendar.getInstance().getTimeInMillis() - (60 * 60 * longArgument * 1000);
                break;
            case 't':
                int longArgumentLength = String.valueOf(longArgument).length();
                if (longArgumentLength == 10) {
                    longArgument *= 1000;
                } else if (longArgumentLength != 13) {
                    return 0;
                }
                timestamp = longArgument;
                break;
            default:
                break;
        }
        return timestamp;
    }

}
