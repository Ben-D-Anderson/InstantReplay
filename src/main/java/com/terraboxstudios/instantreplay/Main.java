package com.terraboxstudios.instantreplay;

import java.sql.SQLException;

import com.terraboxstudios.instantreplay.npc.NPCFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.terraboxstudios.instantreplay.commands.ReplayCommand;
import com.terraboxstudios.instantreplay.events.BlockChangeEvent;
import com.terraboxstudios.instantreplay.events.DeathDamageEvent;
import com.terraboxstudios.instantreplay.events.InteractEvent;
import com.terraboxstudios.instantreplay.events.JoinLeaveEvent;
import com.terraboxstudios.instantreplay.events.PlayerInventoryLogger;
import com.terraboxstudios.instantreplay.events.PlayerMoveLogger;
import com.terraboxstudios.instantreplay.events.RightClickNPCEvent;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.services.EventLoggingService;
import com.terraboxstudios.instantreplay.services.MySQLCleanupService;
import com.terraboxstudios.instantreplay.util.Config;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Config.loadConfig();
		initSQL();
		MySQLCleanupService.start();
		registerEvents();
		registerCommands();
		NPCFactory.getInstance();
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
