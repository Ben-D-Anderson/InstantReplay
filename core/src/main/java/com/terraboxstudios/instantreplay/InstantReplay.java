package com.terraboxstudios.instantreplay;

import com.terraboxstudios.instantreplay.commands.ReplayCommand;
import com.terraboxstudios.instantreplay.events.loggers.*;
import com.terraboxstudios.instantreplay.listeners.PlayerInteractListener;
import com.terraboxstudios.instantreplay.listeners.PlayerRightClickNPCListener;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.services.EventContainerProviderService;
import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.services.MySQLCleanupService;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.ConsoleLogger;
import com.terraboxstudios.instantreplay.util.PluginVersionChecker;
import com.terraboxstudios.instantreplay.versionspecific.VersionSpecificProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Level;


public class InstantReplay extends JavaPlugin {

	@Getter
	private static VersionSpecificProvider versionSpecificProvider;

	static {
		try {
			String packageName = VersionSpecificProvider.class.getPackage().getName();
			String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			versionSpecificProvider = (VersionSpecificProvider) Class.forName(packageName + "." + internalsName + ".VersionSpecificProviderImpl").getConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | InvocationTargetException ignored) {
		}
	}

	@Override
	public void onEnable() {
		if (versionSpecificProvider == null) {
			ConsoleLogger.getInstance().log(Level.SEVERE, "Plugin could not find a valid implementation for this server version.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Config.loadConfig();
		if (!MySQL.getInstance().couldConnect()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		MySQLCleanupService.start();
		registerEvents();
		registerCommands();
		ConsoleLogger.getInstance().log(Level.INFO, "Successfully Enabled InstantReplay.");
		checkPluginVersion();
	}

	@Override
	public void onDisable() {
		ReplayThreads.stopAllThreads();
		EventLoggingService.getInstance().shutdown();
		EventContainerProviderService.getInstance().shutdown();
		MySQLCleanupService.shutdown();
		MySQL.getInstance().closeConnection();
	}

	private void registerCommands() {
		Objects.requireNonNull(getCommand("replay")).setExecutor(new ReplayCommand());
	}

	private void registerEvents() {
		new PlayerMoveLogger();
		new PlayerInventoryLogger();
		registerEvent(new PlayerInteractListener());
		registerEvent(new PlayerJoinLeaveLogger());
		registerEvent(new PlayerDeathDamageLogger());
		registerEvent(new PlayerChangeBlockLogger());
		registerEvent(new PlayerRightClickNPCListener());
	}

	private void registerEvent(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, this);
	}

	private void checkPluginVersion() {
		PluginVersionChecker pluginVersionChecker = new PluginVersionChecker(getDescription().getVersion(), getDescription().getWebsite());
		if (pluginVersionChecker.shouldUpdate()) {
			ConsoleLogger.getInstance().log(Level.WARNING,
					ChatColor.RED + "A new version of InstantReplay is available, please download the latest release from "
							+ ChatColor.YELLOW + pluginVersionChecker.getLatestReleaseUrl());
		}
	}

}
