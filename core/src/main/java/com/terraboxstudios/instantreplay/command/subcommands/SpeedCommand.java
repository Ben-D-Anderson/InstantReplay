package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "speed";
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
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-format-speed"));
            return;
        }
        int speed;
        try {
            speed = Math.abs(Integer.parseInt(args[0]));
        } catch (Exception e) {
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-speed"));
            return;
        }
        if (speed > 10) {
            sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("invalid-argument-speed"));
            return;
        }
        ReplayThreads.getThread(player.getUniqueId()).getContext().setSpeed(speed);
        player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-speed-changed").replace("{SPEED}", speed + ""));
    }

}
