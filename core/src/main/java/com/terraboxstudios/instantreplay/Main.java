package com.terraboxstudios.instantreplay;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

import com.terraboxstudios.instantreplay.events.BlockChangeEvent;
import com.terraboxstudios.instantreplay.events.DeathDamageEvent;
import com.terraboxstudios.instantreplay.events.InteractEvent;
import com.terraboxstudios.instantreplay.events.JoinLeaveEvent;
import com.terraboxstudios.instantreplay.events.PlayerInventoryLogger;
import com.terraboxstudios.instantreplay.events.PlayerMoveLogger;
import com.terraboxstudios.instantreplay.events.RightClickNPCEvent;
import com.terraboxstudios.instantreplay.services.MySQLCleanupService;
import com.terraboxstudios.instantreplay.commands.ReplayCommand;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.threads.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.versionspecific.VersionSpecificProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


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
		initSQL();
		MySQLCleanupService.start();
		registerEvents();
		registerCommands();
	}
	
	@Override
	public void onDisable() {
		ReplayThreads.stopAllThreads();
		EventLoggingService.getInstance().shutdown();
		MySQLCleanupService.shutdown();
		closeSQL();
	}

	private void registerCommands() {
		Objects.requireNonNull(getCommand("replay")).setExecutor(new ReplayCommand());
	}

	private void registerEvents() {
		new PlayerMoveLogger();
		new PlayerInventoryLogger();
		registerEvent(new InteractEvent());
		registerEvent(new JoinLeaveEvent());
		registerEvent(new DeathDamageEvent());
		registerEvent(new BlockChangeEvent());
		registerEvent(new RightClickNPCEvent());
	}

	private void registerEvent(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, this);
	}

	private void initSQL() {
		try {
			new MySQL();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeSQL() {
		try {
			MySQL.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
