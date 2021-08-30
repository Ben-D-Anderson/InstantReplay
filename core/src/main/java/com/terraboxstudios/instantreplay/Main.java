package com.terraboxstudios.instantreplay;

import com.terraboxstudios.instantreplay.commands.ReplayCommand;
import com.terraboxstudios.instantreplay.events.listeners.*;
import com.terraboxstudios.instantreplay.listeners.*;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.services.MySQLCleanupService;
import com.terraboxstudios.instantreplay.services.PlayerInventoryLoggingService;
import com.terraboxstudios.instantreplay.services.PlayerMoveLoggingService;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.versionspecific.VersionSpecificProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public class Main extends JavaPlugin {

	@Getter
	private static VersionSpecificProvider versionSpecificProvider;

	static {
		try {
			String packageName = VersionSpecificProvider.class.getPackage().getName();
			String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			System.out.println(packageName + "." + internalsName);
			versionSpecificProvider = (VersionSpecificProvider) Class.forName(packageName + "." + internalsName).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException exception) {
			Bukkit.getLogger().log(Level.SEVERE, "Plugin could not find a valid implementation for this server version.");
		}
	}

	@Override
	public void onEnable() {
		Config.loadConfig();
		MySQL.getInstance();
		MySQLCleanupService.start();
		registerEvents();
		registerCommands();
	}
	
	@Override
	public void onDisable() {
		ReplayThreads.stopAllThreads();
		EventLoggingService.getInstance().shutdown();
		MySQLCleanupService.shutdown();
		MySQL.getInstance().closeConnection();
	}

	private void registerCommands() {
		Objects.requireNonNull(getCommand("replay")).setExecutor(new ReplayCommand());
	}

	private void registerEvents() {
		new PlayerMoveLoggingService();
		new PlayerInventoryLoggingService();
		registerEvent(new PlayerInteractListener());
		registerEvent(new PlayerJoinLeaveListener());
		registerEvent(new PlayerDeathDamageListener());
		registerEvent(new PlayerChangeBlockListener());
		registerEvent(new PlayerRightClickNPCListener());
	}

	private void registerEvent(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, this);
	}

}
