package net.rankedproject.velocity.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.rankedproject.velocity.VelocityProxy;
import net.rankedproject.velocity.server.ServerTracker;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerConnectionListener {

    private final VelocityProxy plugin;

    @Subscribe
    public void onPlayerConnection(ServerPreConnectEvent event) {
        var proxy = plugin.getProxyServer();
        var servers = proxy.getAllServers();

        if (servers.isEmpty()) return;
        var randomServer = servers.stream()
                .skip((int) (Math.random() * servers.size()))
                .findFirst()
                .orElseThrow();

        event.setResult(ServerPreConnectEvent.ServerResult.allowed(randomServer));
    }
}