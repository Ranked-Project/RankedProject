package net.rankedproject.velocity.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.data.Server;
import net.rankedproject.velocity.VelocityProxy;
import net.rankedproject.velocity.server.data.LoadedServer;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerInstanceFacade {

    private final VelocityProxy plugin;
    private final ServerTracker serverTracker;

    @NotNull
    public LoadedServer createServer(
            @NotNull String identifier,
            @NotNull ServerType serverType,
            @NotNull InetSocketAddress address
    ) {
        var server = new Server(identifier, serverType, address);
        var proxyServerInfo = new ServerInfo(identifier, address);

        var loadedServer = LoadedServer.builder()
                .server(server)
                .serverInfo(proxyServerInfo)
                .build();

        serverTracker.track(loadedServer);
        plugin.getProxyServer().registerServer(proxyServerInfo);

        return loadedServer;
    }

    public void loadServer(@NotNull LoadedServer loadedServer) {
        var serverInfo = loadedServer.getServerInfo();
        plugin.getProxyServer().registerServer(serverInfo);
    }

    public void updateServer(
            @NotNull String identifier,
            @NotNull Consumer<LoadedServer> updateServerConsumer
    ) {
        var loadedServer = serverTracker.get(identifier);
        if (loadedServer == null) return;

        updateServerConsumer.accept(loadedServer);
        serverTracker.track(loadedServer);
    }

    public void removeServer(@NotNull String identifier) {
        var loadedServer = serverTracker.get(identifier);
        if (loadedServer == null) return;

        var proxyServer = plugin.getProxyServer();
        var proxyServerInfo = loadedServer.getServerInfo();

        proxyServer.unregisterServer(proxyServerInfo);
        serverTracker.untrack(loadedServer);
    }
}