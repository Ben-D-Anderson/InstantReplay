package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class HelpCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return "help";
    }

    @Override
    public String[] getNeededPermissions() {
        return new String[]{
                "replay-permission"
        };
    }

    @Override
    public void process(CommandSender sender, String[] args) {
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

}
