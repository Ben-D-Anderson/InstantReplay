package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;

public class ClearlogsCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return "clearlogs";
    }

    @Override
    public String[] getNeededPermissions() {
        return new String[]{
                "replay-permission",
                "replay-clearlogs-permission"
        };
    }

    @Override
    public void process(CommandSender sender, String[] args) {
        MySQL.getInstance().clearLogs();
        sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("logs-cleared"));
    }

}
