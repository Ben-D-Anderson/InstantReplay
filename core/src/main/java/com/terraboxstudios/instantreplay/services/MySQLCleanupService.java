package com.terraboxstudios.instantreplay.services;

import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.util.Config;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MySQLCleanupService {

	private static ScheduledExecutorService service;
	
	public static void start() {
		if (service != null) return;
		service = Executors.newSingleThreadScheduledExecutor();
		String[] tablesToClean = {"block_events", "player_move_events", "death_damage_events", "player_inventory_events", "join_leave_events"};
		service.scheduleWithFixedDelay(() -> {
			try {
				for (String table : tablesToClean) {
					PreparedStatement statement = MySQL.getConnection().prepareStatement
							("DELETE FROM " + table + " WHERE time<=?");
					statement.setLong(1, Calendar.getInstance().getTime().getTime() - (86400000L * Config.getConfig().getLong("settings.days-until-logs-deleted")));
					statement.execute();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}
	
	public static void shutdown() {
		service.shutdown();
	}
	
}