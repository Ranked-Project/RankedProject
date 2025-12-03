package net.rankedproject.velocity.player.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.velocity.server.picker.registry.ServerPickerRegistry;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class PlayerConnectionListener {

    private final ProxyServer proxyServer;
    private final ServerTracker serverTracker;
    private final ServerPickerRegistry serverPickerRegistry;

    /**
     * Handles the initial player connection and assigns the player
     * to an appropriate backend server based on the active server picker.
     *
     * @param event the pre-connect event triggered when a player attempts to connect
     */
    @Subscribe
    public void onPlayerConnection(final ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) {
            return;
        }

        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var serverInfo = this.serverPickerRegistry.get(ServerPickerType.LEAST)
                .getServer(playerUUID, ServerType.RANKED_LOBBY)
                .getServerInfo();
        if (serverInfo == null) {
            return;
        }

        var registeredServer = this.proxyServer.getServer(serverInfo.getName()).orElse(null);
        if (registeredServer == null) {
            return;
        }

        event.setResult(ServerPreConnectEvent.ServerResult.allowed(registeredServer));
    }

    /**
     * Updates the online player count for both the previously connected server
     * and the newly connected server after a player successfully switches servers.
     *
     * @param event the post-connect event containing updated connection context
     */
    @Subscribe
    public void onUpdatePreviousServerOnline(final ServerPostConnectEvent event) {
        var serverConnection = event.getPlayer().getCurrentServer().orElse(null);
        if (serverConnection == null) {
            return;
        }

        var previousServer = serverConnection.getPreviousServer().orElse(null);
        if (previousServer != null) {
            int updatedOnline = previousServer.getPlayersConnected().size();
            this.updateServerOnline(previousServer, updatedOnline);
        }

        var currentServer = serverConnection.getServer();
        if (currentServer != null) {
            int updatedOnline = currentServer.getPlayersConnected().size();
            this.updateServerOnline(currentServer, updatedOnline);
        }
    }

    private void updateServerOnline(
            final @NotNull RegisteredServer registeredServer,
            final int updatedOnline
    ) {
        var currentServerIdentifier = registeredServer.getServerInfo().getName();
        this.serverTracker.update(currentServerIdentifier, loadedServer -> loadedServer.setOnlinePlayers(updatedOnline));
    }
}
