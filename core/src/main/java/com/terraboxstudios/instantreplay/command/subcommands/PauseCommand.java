package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.replay.ReplayInstance;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PauseCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "pause";
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
        ReplayInstance replayInstance = ReplayThreads.getThread(player.getUniqueId());
        if (replayInstance.getPlaying().get()) {
            replayInstance.pauseReplay();
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-paused"));
        } else {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-already-paused"));
        }
    }

}
