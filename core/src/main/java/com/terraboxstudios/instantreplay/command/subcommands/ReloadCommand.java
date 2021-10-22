package com.terraboxstudios.instantreplay.command.subcommands;

import com.terraboxstudios.instantreplay.command.Subcommand;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ReloadCommand implements Subcommand {

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return "reload";
    }

    @Override
    public String[] getNeededPermissions() {
        return new String[]{
                "replay-permission",
                "replay-reload-permission"
        };
    }

    @Override
    public void process(CommandSender sender, String... args) {
        if (!sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings.replay-reload-permission")))) {
            sender.sendMessage(Config.readColouredString("no-permission"));
            return;
        }
        Config.reloadConfig();
        sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("config-reloaded"));
    }

}
