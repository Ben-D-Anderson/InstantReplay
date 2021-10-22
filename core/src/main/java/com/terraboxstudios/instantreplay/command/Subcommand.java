package com.terraboxstudios.instantreplay.command;

import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public interface Subcommand {

    boolean isPlayerOnly();

    String getIdentifier();

    String[] getNeededPermissions();

    void process(CommandSender sender, String[] args);

    default boolean hasPermissions(CommandSender sender) {
        for (String permission : getNeededPermissions()) {
            if (!sender.hasPermission(Objects.requireNonNull(Config.getConfig().getString("settings." + permission))))
                return false;
        }
        return true;
    }

}
