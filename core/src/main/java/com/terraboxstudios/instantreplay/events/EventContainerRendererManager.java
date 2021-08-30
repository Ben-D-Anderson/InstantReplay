package com.terraboxstudios.instantreplay.events;

import com.terraboxstudios.instantreplay.events.containers.*;
import com.terraboxstudios.instantreplay.events.providers.*;
import com.terraboxstudios.instantreplay.events.renderers.*;
import com.terraboxstudios.instantreplay.exceptions.PlayerNotOnlineException;
import com.terraboxstudios.instantreplay.replay.ReplayContext;
import com.terraboxstudios.instantreplay.replay.ReplayThreads;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class EventContainerRendererManager {

    private final ReplayContext context;
    @Getter
    private long currentTimestamp;
    private PlayerChangeBlockEventContainerRenderer playerChangeBlockEventContainerRenderer;
    private PlayerDeathDamageEventContainerRenderer playerDeathDamageEventContainerRenderer;
    private PlayerInventoryEventContainerRenderer playerInventoryEventContainerRenderer;
    private PlayerJoinLeaveEventContainerRenderer playerJoinLeaveEventContainerRenderer;
    private PlayerMoveEventContainerRenderer playerMoveEventContainerRenderer;

    public EventContainerRendererManager(ReplayContext context) {
        this.context = context;
        this.currentTimestamp = getFirstTimestamp(context.getStartTimestamp());
        createRenderers();
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

        playerChangeBlockEventContainerRenderer.render(currentTimestamp);
        playerMoveEventContainerRenderer.render(currentTimestamp);
        playerInventoryEventContainerRenderer.render(currentTimestamp);
        playerDeathDamageEventContainerRenderer.render(currentTimestamp);
        playerJoinLeaveEventContainerRenderer.render(currentTimestamp);

        currentTimestamp += 100;
    }

    public void renderRemainingBlockChanges() {
        Player player = Bukkit.getPlayer(context.getViewer());
        if (player == null) {
            ReplayThreads.stopThread(context.getViewer());
            throw new PlayerNotOnlineException();
        }

        playerChangeBlockEventContainerRenderer.renderRemaining(currentTimestamp);
    }

    public void undoRenderAllBlockChanges() {
        Player player = Bukkit.getPlayer(context.getViewer());
        if (player == null) {
            ReplayThreads.stopThread(context.getViewer());
            throw new PlayerNotOnlineException();
        }

        playerChangeBlockEventContainerRenderer.undoRenderAll();
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