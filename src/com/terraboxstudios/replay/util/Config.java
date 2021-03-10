package com.terraboxstudios.replay.util;

import com.terraboxstudios.replay.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Config {

    private static FileConfiguration config;
    private static File configFile;

    private static final Main plugin = Main.getPlugin(Main.class);

    public static void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        config = plugin.getConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static String readColouredString(String path) {
    	return ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }
    
    public static String readColouredStringList(String path) {
    	List<String> stringList = config.getStringList(path);
    	StringBuilder translated = new StringBuilder();
        for (String str : stringList) {
            translated.append(ChatColor.translateAlternateColorCodes('&', str)).append("\n");
        }
        return translated.toString();
    }
    
}
