package com.terraboxstudios.instantreplay.threads;

import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.containers.*;
import com.terraboxstudios.instantreplay.npc.NPC;
import com.terraboxstudios.instantreplay.npc.NPCFactory;
import com.terraboxstudios.instantreplay.obj.CustomInventory;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReplayInstance extends Thread {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final ArrayList<BlockEventContainer> blockEventsToDo, blockEventsDone;
    private final ArrayList<DeathDamageEventContainer> deathDamageEvents;
    private final ArrayList<JoinLeaveEventContainer> joinLeaveEvents;
    private final ArrayList<PlayerInventoryEventContainer> playerInventoryEvents;
    private final List<PlayerMoveEventContainer> playerMoveEvents;
    private final UUID uuid;
    @Setter
    private int speed;
    private final int radius;
    private final long timeOfCommandRun;
    private long eventTime;
    private Player player;
    @Getter
    private final HashMap<UUID, NPC> npcs = new HashMap<>();
    @Getter
    private final HashMap<UUID, Inventory> inventories = new HashMap<>();
    @Getter
    private final HashMap<UUID, CustomInventory> contentAndArmour = new HashMap<>();
    @Getter
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

    public void stopReplay() throws InterruptedException {
        alive.set(false);
        running.set(false);
        player = Bukkit.getPlayer(uuid);
        if (player != null) {
            for (NPC npc : npcs.values()) {
                npc.deSpawn();
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
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                for (int i = 9; i < 18; i++) {
                                    ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
                                    if (item == null)
                                        item = new ItemStack(Material.AIR, 1);
                                    try {
                                        getInventories().get(playerInventoryObj.getUuid()).setItem(i + 18, item);
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                for (int i = 18; i < 27; i++) {
                                    ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
                                    if (item == null)
                                        item = new ItemStack(Material.AIR, 1);
                                    try {
                                        getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                for (int i = 27; i < 36; i++) {
                                    ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getContents()[i];
                                    if (item == null)
                                        item = new ItemStack(Material.AIR, 1);
                                    try {
                                        getInventories().get(playerInventoryObj.getUuid()).setItem(i - 18, item);
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                for (int i = 36; i < 40; i++) {
                                    ItemStack item = getContentAndArmour().get(playerInventoryObj.getUuid()).getArmourContents()[i - 36];
                                    if (item == null)
                                        item = new ItemStack(Material.AIR, 1);
                                    try {
                                        getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                ItemStack healthItem = getContentAndArmour().get(playerInventoryObj.getUuid()).getHealth()[0];
                                if (healthItem == null)
                                    healthItem = new ItemStack(Material.AIR, 1);
                                try {
                                    getInventories().get(playerInventoryObj.getUuid()).setItem(40, healthItem);
                                } catch (NullPointerException ignored) {
                                }
                                for (int i = 41; i < 45; i++) {
                                    ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
                                    ItemMeta itemmeta = item.getItemMeta();
                                    itemmeta.setDisplayName(" ");
                                    item.setItemMeta(itemmeta);
                                    try {
                                        getInventories().get(playerInventoryObj.getUuid()).setItem(i, item);
                                    } catch (NullPointerException ignored) {
                                    }
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
                                NPC npc = npcs.get(deathDamageEventObj.getUuid());
                                if (npc != null) {
                                    npc.setInvisible(true);
                                    npc.deSpawn();
                                }
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
                                NPC npc = npcs.get(joinLeaveEventObj.getUuid());
                                if (npc != null) {
                                    npc.setInvisible(true);
                                    npc.deSpawn();
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
                        final NPC[] npc = {npcs.get(playerMoveObj.getUuid())};
                        if (!((playerMoveObj.getLocation().getBlockX() >= location.getBlockX() - radius && playerMoveObj.getLocation().getBlockX() <= location.getBlockX() + radius) && (playerMoveObj.getLocation().getBlockZ() >= location.getBlockZ() - radius && playerMoveObj.getLocation().getBlockZ() <= location.getBlockZ() + radius)) && npc[0] != null) {
                            if (!npc[0].isInvisible()) {
                                npc[0].setInvisible(true);
                                npc[0].deSpawn();
                            }
                        } else {
                            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                if (npc[0] == null) {
                                    try {
                                        npc[0] = NPCFactory.getInstance().createNPC(player, playerMoveObj.getName(), playerMoveObj.getUuid());
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                        ReplayThreads.stopThread(uuid);
                                        return;
                                    }

                                    if (npc[0].isInvisible()) {
                                        npc[0].setInvisible(false);
                                    }
                                    npc[0].setLocation(
                                            playerMoveObj.getLocation().getX(),
                                            playerMoveObj.getLocation().getY(),
                                            playerMoveObj.getLocation().getZ(),
                                            playerMoveObj.getLocation().getYaw(),
                                            playerMoveObj.getLocation().getPitch()
                                    );
                                    npcs.put(playerMoveObj.getUuid(), npc[0]);
                                    npc[0].spawn();
                                } else {
                                    if (npc[0].isInvisible()) {
                                        npc[0].setInvisible(false);
                                        npc[0].spawn();
                                    }
                                    npc[0].setLocation(
                                            playerMoveObj.getLocation().getX(),
                                            playerMoveObj.getLocation().getY(),
                                            playerMoveObj.getLocation().getZ(),
                                            playerMoveObj.getLocation().getYaw(),
                                            playerMoveObj.getLocation().getPitch()
                                    );
                                    CustomInventory customInventory = getContentAndArmour().get(playerMoveObj.getUuid());
                                    if (customInventory != null) {
                                        ItemStack item = customInventory.getContents()[customInventory.getHeldSlot()];
                                        if (item == null)
                                            item = new ItemStack(Material.AIR);

                                        npc[0].setItemInHand(item);
                                        npc[0].setEquipmentSlot(0, item);

                                        for (int we = 0; we < 4; we++) {
                                            item = customInventory.getArmourContents()[we];
                                            if (item == null)
                                                item = new ItemStack(Material.AIR);
                                            npc[0].setEquipmentSlot(we + 1, item);
                                        }

                                    }
                                    npc[0].playTeleportPacket();
                                }
                                npc[0].playerHeadPacket();
                            });
                        }
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100 / speed);
                    eventTime += 100;
                } catch (InterruptedException ignored) {
                }
            }
            if (running.get()) {
                player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    ReplayThreads.stopThread(uuid);
                    return;
                }
                for (NPC npc : npcs.values()) {
                    npc.deSpawn();
                }
                player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-finished"));
                ReplayThreads.stopThread(uuid);
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }

}