package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.containers.BlockEventContainer;
import com.terraboxstudios.instantreplay.services.EventLoggingService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Gate;

import java.util.Calendar;

public class BlockChangeEvent implements Listener {
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		Location location = e.getBlock().getLocation();
		Material oldMaterial = Material.AIR;
	    Material newMaterial = e.getBlock().getState().getType();
	    byte oldBlockData = 0;
	    byte newBlockData = e.getBlock().getState().getRawData();
	    EventLoggingService.getInstance().logEvent(new BlockEventContainer(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), oldMaterial, oldBlockData, newMaterial, newBlockData, Calendar.getInstance().getTime().getTime()));
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		Location location = e.getBlock().getLocation();
		Material oldMaterial = e.getBlock().getState().getType();
	    Material newMaterial = Material.AIR;
	    byte oldBlockData = e.getBlock().getState().getRawData();
	    byte newBlockData = 0;
		EventLoggingService.getInstance().logEvent(new BlockEventContainer(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), oldMaterial, oldBlockData, newMaterial, newBlockData, Calendar.getInstance().getTime().getTime()));
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteractGate(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
				e.getClickedBlock() != null &&
				(e.getClickedBlock().getType() == Material.FENCE_GATE || e.getClickedBlock().getType() == Material.TRAP_DOOR) &&
				((e.getPlayer().isSneaking() && e.getItem() == null) || !e.getPlayer().isSneaking())) {
			Gate gate = new Gate(e.getClickedBlock().getState().getRawData());
			gate.setOpen(!gate.isOpen());
			Location location = e.getClickedBlock().getLocation();
			Material oldMaterial = e.getClickedBlock().getState().getType();
		    Material newMaterial = e.getClickedBlock().getState().getType();
		    byte newBlockData = gate.getData();
		    byte oldBlockData = e.getClickedBlock().getState().getRawData();
		    EventLoggingService.getInstance().logEvent(new BlockEventContainer(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), oldMaterial, oldBlockData, newMaterial, newBlockData, Calendar.getInstance().getTime().getTime()));
		}
	}
	
}
