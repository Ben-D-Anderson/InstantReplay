package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.events.containers.*;
import com.terraboxstudios.instantreplay.events.providers.*;
import com.terraboxstudios.instantreplay.events.renderers.*;
import com.terraboxstudios.instantreplay.exceptions.PlayerNotOnlineException;
import com.terraboxstudios.instantreplay.mysql.MySQL;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import com.terraboxstudios.instantreplay.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicLong;

public final class EventContainerRendererManager {

    private final ReplayContext context;
    private final AtomicLong currentTimestamp;
    private PlayerChangeBlockEventContainerRenderer playerChangeBlockEventContainerRenderer;
    private PlayerDeathDamageEventContainerRenderer playerDeathDamageEventContainerRenderer;
    private PlayerInventoryEventContainerRenderer playerInventoryEventContainerRenderer;
    private PlayerJoinLeaveEventContainerRenderer playerJoinLeaveEventContainerRenderer;
    private PlayerMoveEventContainerRenderer playerMoveEventContainerRenderer;

    public EventContainerRendererManager(ReplayContext context) {
        this.context = context;
        this.currentTimestamp = new AtomicLong(getFirstTimestamp(context.getStartTimestamp()));
        createRenderers();
    }

    public long getCurrentTimestamp() {
        return currentTimestamp.get();
    }

    private long getFirstTimestamp(long startTimestamp) {
        startTimestamp -= 100;
        return Math.round(startTimestamp / (double) 100) * 100L;
    }

    public void render() {
        Player player = Bukkit.getPlayer(context.getViewer());
        if (player == null) {
            ReplayThreads.stopThread(context.getViewer());
            throw new PlayerNotOnlineException();
        }

        Utils.runOnMainThread(() -> playerMoveEventContainerRenderer.render(getCurrentTimestamp()));
        playerChangeBlockEventContainerRenderer.render(getCurrentTimestamp());
        playerInventoryEventContainerRenderer.render(getCurrentTimestamp());
        playerDeathDamageEventContainerRenderer.render(getCurrentTimestamp());
        playerJoinLeaveEventContainerRenderer.render(getCurrentTimestamp());

        currentTimestamp.addAndGet(100);
    }

    public void skip(long millis) throws IllegalArgumentException {
        long newTimestamp = getCurrentTimestamp() + millis;
        if (millis <= 0) {
            throw new IllegalArgumentException("Skip time must be positive");
        }
        if (newTimestamp >= context.getTimeOfCommand()) {
            throw new IllegalArgumentException("Skip request lies outside of replay time");
        }
        currentTimestamp.addAndGet(millis);
    }

    public void renderRemainingBlockChanges() {
        Player player = Bukkit.getPlayer(context.getViewer());
        if (player == null) {
            ReplayThreads.stopThread(context.getViewer());
            throw new PlayerNotOnlineException();
        }

        playerChangeBlockEventContainerRenderer.renderRemaining(context);
    }

    public void undoRenderAllBlockChanges() {
        Player player = Bukkit.getPlayer(context.getViewer());
        if (player == null) {
            ReplayThreads.stopThread(context.getViewer());
            throw new PlayerNotOnlineException();
        }

        MySQL.getInstance().undoRenderAllBlocks(playerChangeBlockEventContainerRenderer, context);
    }

    private void createRenderers() {
        createPlayerChangeBlockEventContainerRenderer();
        createPlayerDeathDamageEventContainerRenderer();
        createPlayerInventoryEventContainerRenderer();
        createPlayerJoinLeaveEventContainerRenderer();
        createPlayerMoveEventContainerRenderer();
    }

    private void createPlayerChangeBlockEventContainerRenderer() {
        EventContainerProvider<PlayerChangeBlockEventContainer> provider = new PlayerChangeBlockEventContainerProvider();
        this.playerChangeBlockEventContainerRenderer = new PlayerChangeBlockEventContainerRenderer(context, provider);
    }

    private void createPlayerDeathDamageEventContainerRenderer() {
        EventContainerProvider<PlayerDeathDamageEventContainer> provider = new PlayerDeathDamageEventContainerProvider();
        this.playerDeathDamageEventContainerRenderer = new PlayerDeathDamageEventContainerRenderer(context, provider);
    }

    private void createPlayerInventoryEventContainerRenderer() {
        EventContainerProvider<PlayerInventoryEventContainer> provider = new PlayerInventoryEventContainerProvider();
        this.playerInventoryEventContainerRenderer = new PlayerInventoryEventContainerRenderer(context, provider);
    }

    private void createPlayerJoinLeaveEventContainerRenderer() {
        EventContainerProvider<PlayerJoinLeaveEventContainer> provider = new PlayerJoinLeaveEventContainerProvider();
        this.playerJoinLeaveEventContainerRenderer = new PlayerJoinLeaveEventContainerRenderer(context, provider);
    }

    private void createPlayerMoveEventContainerRenderer() {
        EventContainerProvider<PlayerMoveEventContainer> provider = new PlayerMoveEventContainerProvider();
        this.playerMoveEventContainerRenderer = new PlayerMoveEventContainerRenderer(context, provider);
    }
}
