package com.terraboxstudios.instantreplay.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ReplaySubcommandTabCompleter implements TabCompleter {

    private final ReplayCommand replayCommand;

    public ReplaySubcommandTabCompleter(ReplayCommand replayCommand) {
        this.replayCommand = replayCommand;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1)
            return null;

        List<String> suggestions = new ArrayList<>();
        for (Subcommand subcommand : replayCommand.getSubcommands().values()) {
            if (hasSubcommandPermissions(subcommand, sender)) {
                suggestions.add(subcommand.getIdentifier());
            }
        }
        return suggestions;
    }

    private boolean hasSubcommandPermissions(Subcommand subcommand, CommandSender sender) {
        for (String permission : subcommand.getNeededPermissions()) {
            if (!sender.hasPermission(permission))
                return false;
        }
        return true;
    }

}
