package com.terraboxstudios.instantreplay.util;

import com.terraboxstudios.instantreplay.InstantReplay;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Utils {

	public static Optional<ZonedDateTime> convertToTimestamp(String argument, ZoneId zoneId) {
		String datetimePattern = Objects.requireNonNull(Config.getConfig().getString("settings.timestamp-converter-format-datetime"));
		try {
			ZonedDateTime zonedDateTime = LocalDateTime.parse(argument, DateTimeFormatter.ofPattern(datetimePattern)).atZone(zoneId);
			return Optional.of(zonedDateTime);
		} catch (Exception ignored) {
		}

		String timePattern = Objects.requireNonNull(Config.getConfig().getString("settings.timestamp-converter-format-time"));
		try {
			LocalTime localTime = LocalTime.parse(argument, DateTimeFormatter.ofPattern(timePattern));
			ZonedDateTime zonedDateTime = localTime.atDate(LocalDate.now(zoneId)).atZone(zoneId);
			return Optional.of(zonedDateTime);
		} catch (Exception ignored) {
		}

		return Optional.empty();
	}

	public static String getReplayPrefix() {
		return Config.getConfig().getBoolean("settings.use-plugin-prefix") ? Config.readColouredString("plugin-prefix") : "";
	}

	public static void sendReplayTimestampMessage(Player player, long timestamp) {
		TextComponent component = new TextComponent(TextComponent.fromLegacyText(Utils.getReplayPrefix() + Config.readColouredString("replay-timestamp-output").replace("{TIMESTAMP}", String.valueOf(timestamp))));
		component.setClickEvent(InstantReplay.getVersionSpecificProvider().getUtilsHelper().getTimestampMessageClickEvent(timestamp));
		player.spigot().sendMessage(component);
	}

	public static void sendTimestampMessage(CommandSender sender, long timestamp) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("timestamp-output").replace("{TIMESTAMP}", String.valueOf(timestamp)));
			return;
		}
		Player player = (Player) sender;
		TextComponent component = new TextComponent(TextComponent.fromLegacyText(Utils.getReplayPrefix() + Config.readColouredString("timestamp-output").replace("{TIMESTAMP}", String.valueOf(timestamp))));
		component.setClickEvent(InstantReplay.getVersionSpecificProvider().getUtilsHelper().getTimestampMessageClickEvent(timestamp));
		player.spigot().sendMessage(component);
	}

	public static void runOnMainThread(Runnable runnable) {
		Bukkit.getScheduler().runTask(InstantReplay.getPlugin(InstantReplay.class), runnable);
	}

	public static boolean isLocationInReplay(Location locationOne, Location locationTwo, int radius) {
		if (locationOne.getWorld() == null || locationTwo.getWorld() == null) return false;
		return (locationOne.getBlockX() >= locationTwo.getBlockX() - radius && locationOne.getBlockX() <= locationTwo.getBlockX() + radius)
				&& (locationOne.getBlockZ() >= locationTwo.getBlockZ() - radius && locationOne.getBlockZ() <= locationTwo.getBlockZ() + radius)
				&& locationOne.getWorld().getName().equals(locationTwo.getWorld().getName())
				&& (Config.getConfig().getBoolean("settings.ignore-y-radius") || (locationOne.getBlockY() >= locationTwo.getBlockY() - radius && locationOne.getBlockY() <= locationTwo.getBlockY() + radius));
	}

	public static long roundTime(long time) {
		return Math.round(time * (double) 100) / 100L;
	}

	public static double roundTwoDP(double value) {
		return Math.round(value * (double) 100) / 100D;
	}

}
