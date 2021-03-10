package com.terraboxstudios.replay.threads;

import com.terraboxstudios.replay.Main;
import com.terraboxstudios.replay.containers.*;
import com.terraboxstudios.replay.obj.CustomInventory;
import com.terraboxstudios.replay.util.Config;
import com.terraboxstudios.replay.util.SkinCache;
import com.terraboxstudios.replay.util.Utils;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ReplayInstance extends Thread {

	private final AtomicBoolean running = new AtomicBoolean(true);
	private final AtomicBoolean alive = new AtomicBoolean(true);
	private final ArrayList<BlockEventContainer> blockEventsToDo, blockEventsDone;
	private final ArrayList<DeathDamageEventContainer> deathDamageEvents;
	private final ArrayList<JoinLeaveEventContainer> joinLeaveEvents;
	private final ArrayList<PlayerInventoryEventContainer> playerInventoryEvents;
	private final List<PlayerMoveEventContainer> playerMoveEvents;
	private final UUID uuid;
	private int speed;
	private final int radius;
	private final long timeOfCommandRun;
	private long eventTime;
	private Player player;
	private final HashMap<UUID, EntityPlayer> npcs = new HashMap<>();
	public HashMap<UUID, EntityPlayer> getNpcs() {
		return npcs;
	}
	private final HashMap<UUID, Inventory> inventories = new HashMap<>();
	private final HashMap<UUID, CustomInventory> contentAndArmour = new HashMap<>();
	public HashMap<UUID, CustomInventory> getContentAndArmour() {
		return contentAndArmour;
	}
	private final Location location;

	public ReplayInstance(ArrayList<BlockEventContainer> blockEventsToDo, List<PlayerMoveEventContainer> playerMoveEvents, ArrayList<DeathDamageEventContainer> deathDamageEvents, ArrayList<JoinLeaveEventContainer> joinLeaveEvents, ArrayList<PlayerInventoryEventContainer> playerInventoryEvents, UUID uuid, int speed, int time, long timeStamp, long timeOfCommandRun, int radius, Location location) {
		this.blockEventsToDo = blockEventsToDo;
		this.blockEventsDone = new ArrayList<>();
		this.playerMoveEvents = Utils.sortPlayerMoveEventsByTime(playerMoveEvents);
		this.deathDamageEvents = deathDamageEvents;
		this.joinLeaveEvents = joinLeaveEvents;
		this.playerInventoryEvents = playerInventoryEvents;
		this.uuid = uuid;
		this.speed = speed;
		this.radius = radius;
		this.timeOfCommandRun = timeOfCommandRun;
		this.location = location;
		player = Bukkit.getPlayer(uuid);
		if (player == null) {
			ReplayThreads.stopThread(uuid);
			return;
		}
		if (timeStamp < 1) {
			player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-starting").replace("{TIME}", time + "").replace("{SPEED}", speed + "").replace("{RADIUS}", radius + ""));
		} else {
			player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-starting-with-timestamp").replace("{TIMESTAMP}", timeStamp + "").replace("{SPEED}", speed + "").replace("{RADIUS}", radius + ""));
		}
		if (timeStamp < 1) {
			eventTime = Calendar.getInstance().getTime().getTime() - (time * 1000L);
		} else {
			eventTime = timeStamp;
		}
		eventTime -= 100;
		eventTime = Long.parseLong(new DecimalFormat("#").format(eventTime / 100)) * 100;
		start();
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Location getLocation() {
		return location;
	}

	@SuppressWarnings("deprecation")
	public void stopReplay() throws InterruptedException {
		alive.set(false);
		running.set(false);
		player = Bukkit.getPlayer(uuid);
		if (player != null) {
			for (UUID uuid : npcs.keySet()) {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npcs.get(uuid).getId()));
			}
			if (blockEventsDone != null && !blockEventsDone.isEmpty()) {
				for (BlockEventContainer b : blockEventsDone) {
					blockEventsToDo.remove(b);
				}
				for (BlockEventContainer blockEventObj : blockEventsToDo) {
					player.sendBlockChange(blockEventObj.getLoc(), blockEventObj.getNewBlockMaterial(), blockEventObj.getNewBlockData());
				}
			}
		}
		interrupt();
		join();
	}

	public void pauseReplay() {
		running.set(false);
	}

	public boolean isRunning() {
		return running.get();
	}

	public void resumeReplay() {
		running.set(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		int timestampI = 0;
		while (alive.get()) {
			while (eventTime < timeOfCommandRun && running.get()) {
				timestampI++;
				if (timestampI >= Config.getConfig().getInt("settings.seconds-per-timestamp-output") * 10) {
					player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-timestamp-output").replace("{TIMESTAMP}", eventTime + ""));
					timestampI = 0;
				}
				for (PlayerInventoryEventContainer playerInventoryObj : playerInventoryEvents) {
					if (playerInventoryObj.getTime() == eventTime) {
						player = Bukkit.getPlayer(uuid);
						if (player == null) {
							ReplayThreads.stopThread(uuid);
							return;
						}
						contentAndArmour.put(playerInventoryObj.getUuid(), new CustomInventory(playerInventoryObj.getContents(), playerInventoryObj.getArmourContents(), playerInventoryObj.getHealth(), playerInventoryObj.getHeldSlot()));
						if (getInventories().get(playerInventoryObj.getUuid()) != null) {
							if (player.getOpenInventory().getTopInventory().getTitle().equals(playerInventoryObj.getName() + "'s Inventory")) {
								for (int i = 0; i < 9; i++) {
									ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									try {
										getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);										
									} catch (NullPointerException ignored) {}
								}
								for (int i = 9; i < 18; i++) {
									ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									try {
										getInventories().get(playerInventoryObj.getUuid()).setItem(i + 18, item);										
									} catch (NullPointerException ignored) {}
								}
								for (int i = 18; i < 27; i++) {
									ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									try {
										getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);										
									} catch (NullPointerException ignored) {}
								}
								for (int i = 27; i < 36; i++) {
									ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									try {
										getInventories().get(playerInventoryObj.getUuid()).setItem(i - 18, item);										
									} catch (NullPointerException ignored) {}
								}
								for (int i = 36; i < 40; i++) {
									ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getArmourContents()[i - 36];
									if (item == null)
										item = new ItemStack(Material.AIR, 1);
									try {
										getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);
									} catch (NullPointerException ignored) {}
								}
								ItemStack healthItem = getContentAndArmour().get(playerInventoryObj.getUuid()).getHealth()[0];
								if (healthItem == null)
									healthItem = new ItemStack(Material.AIR, 1);
								try {
									getInventories().get(playerInventoryObj.getUuid()).setItem(40, healthItem);
								} catch (NullPointerException ignored) {}
								for (int i = 41; i < 45; i++) {
									ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
									ItemMeta itemmeta = item.getItemMeta();
									itemmeta.setDisplayName(" ");
									item.setItemMeta(itemmeta);
									try {
										getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);										
									} catch (NullPointerException ignored) {}
								}
								player.getOpenInventory().getTopInventory().setContents(getInventories().get(playerInventoryObj.getUuid()).getContents());
							}
						}
					}
				}
				for (BlockEventContainer blockEventObj : blockEventsToDo) {
					if (blockEventObj.getTime() == eventTime) {
						player = Bukkit.getPlayer(uuid);
						if (player == null) {
							ReplayThreads.stopThread(uuid);
							return;
						}
						player.sendBlockChange(blockEventObj.getLoc(), blockEventObj.getNewBlockMaterial(), blockEventObj.getNewBlockData());
						blockEventsDone.add(blockEventObj);
					}
				}
				for (DeathDamageEventContainer deathDamageEventObj : deathDamageEvents) {
					if (deathDamageEventObj.getTime() == eventTime) {
						player = Bukkit.getPlayer(uuid);
						if (player == null) {
							ReplayThreads.stopThread(uuid);
							return;
						}
						if (deathDamageEventObj.getType().equalsIgnoreCase("DAMAGE")) {
							player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("player-damage").replace("{PLAYER}", deathDamageEventObj.getName()).replace("{SOURCE}", deathDamageEventObj.getSource()));
						}
						if (deathDamageEventObj.getType().equalsIgnoreCase("DEATH")) {
							player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("player-death").replace("{PLAYER}", deathDamageEventObj.getName()).replace("{SOURCE}", deathDamageEventObj.getSource()));
							Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
								npcs.get(deathDamageEventObj.getUuid()).setInvisible(true);
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npcs.get(deathDamageEventObj.getUuid()).getId()));
							});
						}
					}
				}
				for (JoinLeaveEventContainer joinLeaveEventObj : joinLeaveEvents) {
					if (joinLeaveEventObj.getTime() == eventTime) {
						player = Bukkit.getPlayer(uuid);
						if (player == null) {
							ReplayThreads.stopThread(uuid);
							return;
						}
						if (joinLeaveEventObj.getType().equalsIgnoreCase("JOIN")) {
							player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("player-join").replace("{PLAYER}", joinLeaveEventObj.getName()));
						}
						if (joinLeaveEventObj.getType().equalsIgnoreCase("LEAVE")) {
							player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("player-leave").replace("{PLAYER}", joinLeaveEventObj.getName()));
							Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
								if (npcs.get(joinLeaveEventObj.getUuid()) != null) {
									npcs.get(joinLeaveEventObj.getUuid()).setInvisible(true);
									((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npcs.get(joinLeaveEventObj.getUuid()).getId()));									
								}
							});
						}
					}
				}
				for (PlayerMoveEventContainer playerMoveObj : playerMoveEvents) {
					if (playerMoveObj.getTime() == eventTime) {
						player = Bukkit.getPlayer(uuid);
						if (player == null) {
							ReplayThreads.stopThread(uuid);
							return;
						}
						if (!((playerMoveObj.getLocation().getBlockX() >= location.getBlockX() - radius && playerMoveObj.getLocation().getBlockX() <= location.getBlockX() + radius) && (playerMoveObj.getLocation().getBlockZ() >= location.getBlockZ() - radius && playerMoveObj.getLocation().getBlockZ() <= location.getBlockZ() + radius)) && npcs.get(playerMoveObj.getUuid()) != null) {
							if (!npcs.get(playerMoveObj.getUuid()).isInvisible()) {
								npcs.get(playerMoveObj.getUuid()).setInvisible(true);
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npcs.get(playerMoveObj.getUuid()).getId()));
							}
						} else {
							Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
								if (npcs.get(playerMoveObj.getUuid()) == null) {
									MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
									WorldServer world = ((CraftWorld) playerMoveObj.getLocation().getWorld()).getHandle();
									GameProfile gameProfile = new GameProfile(UUID.randomUUID(), playerMoveObj.getName());
									setSkin(gameProfile, playerMoveObj.getUuid());
									EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
									if (npc.isInvisible()) {
										npc.setInvisible(false);
										((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
									}
									npc.setLocation(
											playerMoveObj.getLocation().getX(),
											playerMoveObj.getLocation().getY(),
											playerMoveObj.getLocation().getZ(),
											playerMoveObj.getLocation().getYaw(),
											playerMoveObj.getLocation().getPitch());
									npcs.put(playerMoveObj.getUuid(), npc);
									((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npcs.get(playerMoveObj.getUuid())));
								} else {
									if (npcs.get(playerMoveObj.getUuid()).isInvisible()) {
										npcs.get(playerMoveObj.getUuid()).setInvisible(false);
										((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npcs.get(playerMoveObj.getUuid())));
									}
									npcs.get(playerMoveObj.getUuid()).setLocation(
											playerMoveObj.getLocation().getX(),
											playerMoveObj.getLocation().getY(),
											playerMoveObj.getLocation().getZ(),
											playerMoveObj.getLocation().getYaw(),
											playerMoveObj.getLocation().getPitch());
									if (getContentAndArmour().get(playerMoveObj.getUuid()) != null) {
										ItemStack item = getContentAndArmour().get(playerMoveObj.getUuid()).getContents()[getContentAndArmour().get(playerMoveObj.getUuid()).getHeldSlot()];
										if (item == null)
											item = new ItemStack(Material.AIR);

										npcs.get(playerMoveObj.getUuid()).inventory.setCarried(CraftItemStack.asNMSCopy(item));
										((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(npcs.get(playerMoveObj.getUuid()).getId(), 0, CraftItemStack.asNMSCopy(item)));

										for (int we = 0; we < 4; we++) {
											item = getContentAndArmour().get(playerMoveObj.getUuid()).getArmourContents()[we];
											if (item == null)
												item = new ItemStack(Material.AIR);
											((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(npcs.get(playerMoveObj.getUuid()).getId(), we + 1, CraftItemStack.asNMSCopy(item)));
										}

									}
									((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(npcs.get(playerMoveObj.getUuid())));
								}
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(npcs.get(playerMoveObj.getUuid()), (byte) (npcs.get(playerMoveObj.getUuid()).yaw * 256 / 360)));
							});
						}
					}
				}
				try {
					TimeUnit.MILLISECONDS.sleep(100 / speed);
					eventTime += 100;
				} catch (InterruptedException ignored) {}
			}
			if (running.get()) {
				player = Bukkit.getPlayer(uuid);
				if (player == null) {
					ReplayThreads.stopThread(uuid);
					return;
				}
				for (UUID uuid : npcs.keySet()) {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npcs.get(uuid).getId()));
				}
				player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-finished"));
				ReplayThreads.stopThread(uuid);
				return;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException ignored) {}
		}
	}

	private void setSkin(GameProfile profile, UUID uuid) {
		try {
			if (SkinCache.isSkinCached(uuid)) {
				profile.getProperties().put("textures", SkinCache.getCachedSkin(uuid));
				return;
			}
			HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				String reply = convertStreamToString(connection.getInputStream());
				String skin, signature;
				try {
					skin = reply.split("\"value\":\"")[1].split("\"")[0];
					signature = reply.split("\"signature\":\"")[1].split("\"")[0];
				} catch (ArrayIndexOutOfBoundsException e) {
					skin = reply.split("\"value\" : \"")[1].split("\"")[0];
					signature = reply.split("\"signature\" : \"")[1].split("\"")[0];
				}
				Property property = new Property("textures", skin, signature);
				profile.getProperties().put("textures", property);
				connection.disconnect();
				SkinCache.addToCachedSkins(uuid, property);
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> SkinCache.removeCachedSkin(uuid), 60 * 20L);
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "[REPLAY ERROR] Could not load skin for " + profile.getName() + " - Reason: " + connection.getResponseMessage());
			}
		} catch (IOException ignored) {
		}
	}

	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public HashMap<UUID, Inventory> getInventories() {
		return inventories;
	}

}