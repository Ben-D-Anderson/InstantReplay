package com.terraboxstudios.instantreplay.replay;

import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.containers.*;
import com.terraboxstudios.instantreplay.inventory.CustomInventory;
import com.terraboxstudios.instantreplay.inventory.InventoryFactory;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReplayInstance extends Thread {

    private final AtomicBoolean playing = new AtomicBoolean(true);
    private final AtomicBoolean alive = new AtomicBoolean(true);
    //todo refactor to use EventRenderers
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

    //todo refactor to use Joshua Bloch's builder design pattern
    public ReplayInstance(ArrayList<BlockEventContainer> blockEventsToDo, List<PlayerMoveEventContainer> playerMoveEvents, ArrayList<DeathDamageEventContainer> deathDamageEvents, ArrayList<JoinLeaveEventContainer> joinLeaveEvents, ArrayList<PlayerInventoryEventContainer> playerInventoryEvents, UUID uuid, int speed, int time, long timeStamp, long timeOfCommandRun, int radius, Location location) {
        this.blockEventsToDo = blockEventsToDo;
        this.blockEventsDone = new ArrayList<>();
        playerMoveEvents.sort(null);
        this.playerMoveEvents = playerMoveEvents;
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
            eventTime = Calendar.getInstance().getTimeInMillis() - (time * 1000L);
        } else {
            player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-starting-with-timestamp").replace("{TIMESTAMP}", timeStamp + "").replace("{SPEED}", speed + "").replace("{RADIUS}", radius + ""));
            eventTime = timeStamp;
        }
        eventTime -= 100;
        eventTime = Long.parseLong(new DecimalFormat("#").format(eventTime / 100)) * 100;
        start();
    }

    public void stopReplay() {
        alive.set(false);
        playing.set(false);
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
                    player.sendBlockChange(blockEventObj.getLocation(), blockEventObj.getNewBlockMaterial(), blockEventObj.getNewBlockData());
                }
            }
        }
    }

    public void pauseReplay() {
        playing.set(false);
    }

    public boolean isPlaying() {
        return playing.get();
    }

    public void resumeReplay() {
        playing.set(true);
    }

    @Override
    public void run() {
        int timestampI = 0;
        while (alive.get()) {
            while (eventTime < timeOfCommandRun && playing.get()) {
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

                        CustomInventory newCustomInventory = new CustomInventory(playerInventoryObj.getContents(), playerInventoryObj.getArmourContents(), playerInventoryObj.getHealth(), playerInventoryObj.getHeldSlot());
                        CustomInventory previousInventory = contentAndArmour.get(playerInventoryObj.getUuid());
                        if (previousInventory != null && previousInventory.equals(newCustomInventory)) continue;

                        contentAndArmour.put(playerInventoryObj.getUuid(), newCustomInventory);
                        Inventory npcInv = getInventories().get(playerInventoryObj.getUuid());
                        if (npcInv == null) {
                            getInventories().put(playerInventoryObj.getUuid(),
                                    InventoryFactory.getInstance().createNPCInventory(
                                            getContentAndArmour().get(playerInventoryObj.getUuid()),
                                            playerInventoryObj.getName()
                                    )
                            );
                        } else {
                            InventoryFactory.getInstance().updateNPCInventory(
                                    npcInv,
                                    getContentAndArmour().get(playerInventoryObj.getUuid())
                            );
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
                        player.sendBlockChange(blockEventObj.getLocation(), blockEventObj.getNewBlockMaterial(), blockEventObj.getNewBlockData());
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
                                    NPCSkin<?> skin = Main.getVersionSpecificProvider().getNPCFactory()
                                            .getSkin(playerMoveObj.getUuid(), playerMoveObj.getName());
                                    npc[0] = Main.getVersionSpecificProvider().getNPCFactory().createNPC(
                                            player.getUniqueId(),
                                            playerMoveObj.getUuid(),
                                            playerMoveObj.getName(),
                                            skin,
                                            playerMoveObj.getWorld()
                                    );
                                    npc[0].spawn(playerMoveObj.getLocation());
                                    npcs.put(playerMoveObj.getUuid(), npc[0]);
                                } else {
                                    if (npc[0].isInvisible()) {
                                        npc[0].setInvisible(false);
                                    }
                                    if (!npc[0].isSpawned()) {
                                        npc[0].spawn(playerMoveObj.getLocation());
                                    }
                                    npc[0].moveTo(playerMoveObj.getLocation());
                                    CustomInventory customInventory = getContentAndArmour().get(playerMoveObj.getUuid());
                                    if (customInventory != null) {
                                        ItemStack item = customInventory.getContents()[customInventory.getHeldSlot()];
                                        if (item == null)
                                            item = new ItemStack(Material.AIR);

                                        npc[0].setItemInMainHand(item);
                                        npc[0].setEquipmentSlot(0, item);

                                        for (int we = 0; we < 4; we++) {
                                            item = customInventory.getArmourContents()[we];
                                            if (item == null)
                                                item = new ItemStack(Material.AIR);
                                            npc[0].setEquipmentSlot(we + 1, item);
                                        }

                                    }
                                }
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
            if (playing.get()) {
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