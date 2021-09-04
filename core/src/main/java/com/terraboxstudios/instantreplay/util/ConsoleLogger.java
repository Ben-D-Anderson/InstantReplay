package com.terraboxstudios.instantreplay.util;

import com.terraboxstudios.instantreplay.InstantReplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ConsoleLogger {

    private final JavaPlugin plugin;
    private static ConsoleLogger instance;

    private ConsoleLogger() {
        plugin = InstantReplay.getPlugin(InstantReplay.class);
    }

    public static ConsoleLogger getInstance() {
        if (instance == null) instance = new ConsoleLogger();
        return instance;
    }

    public void log(Level level, String message) {
        plugin.getLogger().log(level, message);
    }

}
