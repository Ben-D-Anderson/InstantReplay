package com.terraboxstudios.instantreplay.command;

import com.terraboxstudios.instantreplay.command.subcommands.*;
import com.terraboxstudios.instantreplay.util.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ReplayCommand implements CommandExecutor {

    private final Map<String, Subcommand> subcommands;

    public ReplayCommand() {
        subcommands = new TreeMap<>();
        registerSubcommand(new HelpCommand());
        registerSubcommand(new ReloadCommand());
        registerSubcommand(new ClearlogsCommand());
        registerSubcommand(new TimestampCommand());
        registerSubcommand(new StopCommand());
        registerSubcommand(new SpeedCommand());
        registerSubcommand(new PauseCommand());
        registerSubcommand(new ResumeCommand());
        registerSubcommand(new StartCommand());
        registerSubcommand(new SkipCommand());
    }

    private void registerSubcommand(Subcommand subcommand) {
        subcommands.put(subcommand.getIdentifier(), subcommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            args = new String[]{"help"};
        }

        String action = args[0].toLowerCase();
        String[] subcommandArguments;
        try {
            subcommandArguments = Arrays.copyOfRange(args, 1, args.length);
        } catch (Exception e) {
            subcommandArguments = new String[0];
        }

        Subcommand subcommand;
        if (!subcommands.containsKey(action)) {
            subcommand = subcommands.get("help");
        } else {
            subcommand = subcommands.get(action);
        }
        if (!subcommand.hasPermissions(sender)) {
            sender.sendMessage(Config.readColouredString("no-permission"));
            return true;
        }
        if (subcommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command Only Allowed For Players");
            return true;
        }
        subcommand.process(sender, subcommandArguments);
        return true;
    }

}
