package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.replay.ReplayInstance;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResumeCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "resume";
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
        if (!replayInstance.getPlaying().get()) {
            replayInstance.resumeReplay();
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-resumed"));
        } else {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-not-paused"));
        }
    }

}
