package com.terraboxstudios.instantreplay.replay;

import com.terraboxstudios.instantreplay.events.EventContainerRendererManager;
import com.terraboxstudios.instantreplay.util.Config;
import com.terraboxstudios.instantreplay.util.Utils;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class ReplayInstance extends Thread {

    private final AtomicBoolean playing;
    private final AtomicBoolean alive;
    private final ReplayContext context;
    private final EventContainerRendererManager rendererManager;

    public ReplayInstance(ReplayContext context) {
        this.playing = new AtomicBoolean(true);
        this.alive = new AtomicBoolean(true);
        this.context = context;
        this.rendererManager = new EventContainerRendererManager(context);
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player == null) {
            ReplayThreads.stopThread(getContext().getViewer());
            return;
        }
        player.sendMessage(Utils.getReplayPrefix() + Config.readColouredString("replay-starting-with-timestamp")
                .replace("{TIMESTAMP}", getRendererManager().getCurrentTimestamp() + "")
                .replace("{SPEED}", getContext().getSpeed() + "")
                .replace("{RADIUS}", getContext().getRadius() + ""));
        getRendererManager().undoRenderAllBlockChanges();
        start();
    }

    public void stopReplay() {
        alive.set(false);
        playing.set(false);
        Player player = Bukkit.getPlayer(getContext().getViewer());
        if (player != null) {
            for (NPC npc : getContext().getNpcMap().values()) {
                npc.deSpawn();
            }
            getRendererManager().renderRemainingBlockChanges();
        }
    }

    public void pauseReplay() {
        playing.set(false);
    }

    public void resumeReplay() {
        playing.set(true);
    }

    @Override
    public void run() {
        int timestampPrintTimer = 0;
        while (alive.get()) {
            while (playing.get()) {
                Player player = Bukkit.getPlayer(getContext().getViewer());
                if (player == null) {
                    ReplayThreads.stopThread(getContext().getViewer());
                    return;
                }

                timestampPrintTimer++;
                if (timestampPrintTimer >= Config.getConfig().getInt("settings.seconds-per-timestamp-output") * 10) {
                    Utils.sendReplayTimestampMessage(player, timestampPrintTimer);
                    timestampPrintTimer = 0;
                }

                getRendererManager().render();

                try {
                    TimeUnit.MILLISECONDS.sleep(100 / getContext().getSpeed());
                } catch (InterruptedException ignored) {}
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

}