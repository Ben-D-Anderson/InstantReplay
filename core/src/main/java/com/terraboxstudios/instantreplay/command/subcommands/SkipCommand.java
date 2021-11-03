package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.events.EventContainerRendererManager;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "skip";
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
        if (!ReplayThreads.isUserReplaying(player.getUniqueId())) {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("no-active-replay"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-skip"));
            return;
        }
        long millis;
        try {
            millis = parseTimeArgument(args[0]);
        } catch (Exception e) {
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-skip"));
            return;
        }
        EventContainerRendererManager rendererManager = ReplayThreads.getThread(player.getUniqueId()).getRendererManager();
        try {
            rendererManager.skip(millis);
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-time-skip"));
            return;
        }
        player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-skipped")
                .replace("{SECONDS}", millis / 1000 + "")
                .replace("{TIMESTAMP}", rendererManager.getCurrentTimestamp() + ""));
    }

    private long parseTimeArgument(String argument) {
        long longArgument = Long.parseLong(argument.substring(0, argument.length() - 1));
        if (longArgument <= 0) throw new IllegalArgumentException("Skip time must be positive");
        char character = Character.toLowerCase(argument.charAt(argument.length() - 1));
        if (character == 'm') {
            return longArgument * 60 * 1000;
        } else if (character == 's') {
            return longArgument * 1000;
        } else {
            throw new IllegalArgumentException("Invalid time argument");
        }
    }

}
