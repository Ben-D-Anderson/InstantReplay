package com.terraboxstudios.replay;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.terraboxstudios.replay.commands.ReplayCommand;
import com.terraboxstudios.replay.events.BlockChangeEvent;
import com.terraboxstudios.replay.events.DeathDamageEvent;
import com.terraboxstudios.replay.events.InteractEvent;
import com.terraboxstudios.replay.events.JoinLeaveEvent;
import com.terraboxstudios.replay.events.PlayerInventoryLogger;
import com.terraboxstudios.replay.events.PlayerMoveLogger;
import com.terraboxstudios.replay.events.RightClickNPCEvent;
import com.terraboxstudios.replay.mysql.MySQL;
import com.terraboxstudios.replay.services.EventLoggingService;
import com.terraboxstudios.replay.services.MySQLCleanupService;
import com.terraboxstudios.replay.util.Config;

public class Main extends JavaPlugin {
	
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
		EventLoggingService.getInstance().shutdown();
		MySQLCleanupService.shutdown();
		try {
			MySQL.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void registerEvents() {
		new PlayerMoveLogger();
		new PlayerInventoryLogger();
		regEvent(new InteractEvent());
		regEvent(new JoinLeaveEvent());
		regEvent(new DeathDamageEvent());
		regEvent(new BlockChangeEvent());
		regEvent(new RightClickNPCEvent());
	}
	

	private void registerCommands() {
		getCommand("replay").setExecutor(new ReplayCommand());
	}
	
	private void initSQL() {
		try {
			new MySQL();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void regEvent(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, this);
	}

}
