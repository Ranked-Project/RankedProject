package net.rankedproject.velocity.player.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
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

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerConnectionListener {

    private final ProxyServer proxyServer;
    private final ServerTracker serverTracker;
    private final ServerPickerRegistry serverPickerRegistry;

    @Subscribe
    public void onPlayerConnection(ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) {
            return;
        }

        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var serverInfo = serverPickerRegistry.get(ServerPickerType.LEAST)
                .getServer(playerUUID, ServerType.RANKED_LOBBY)
                .getServerInfo();
        if (serverInfo == null) {
            return;
        }

        var registeredServer = proxyServer.getServer(serverInfo.getName()).orElse(null);
        if (registeredServer == null) {
            return;
        }

        event.setResult(ServerPreConnectEvent.ServerResult.allowed(registeredServer));
    }

    @Subscribe
    public void onUpdatePreviousServerOnline(ServerPostConnectEvent event) {
        var serverConnection = event.getPlayer().getCurrentServer().orElse(null);
        if (serverConnection == null) {
            return;
        }

        var previousServer = serverConnection.getPreviousServer().orElse(null);
        if (previousServer != null) {
            int updatedOnline = previousServer.getPlayersConnected().size();
            updateServerOnline(previousServer, updatedOnline);
        }

        var currentServer = serverConnection.getServer();
        if (currentServer != null) {
            int updatedOnline = currentServer.getPlayersConnected().size();
            updateServerOnline(currentServer, updatedOnline);
        }
    }

    private void updateServerOnline(RegisteredServer registeredServer, int updatedOnline) {
        var currentServerIdentifier = registeredServer.getServerInfo().getName();
        serverTracker.update(currentServerIdentifier, loadedServer -> loadedServer.setOnlinePlayers(updatedOnline));
    }
}