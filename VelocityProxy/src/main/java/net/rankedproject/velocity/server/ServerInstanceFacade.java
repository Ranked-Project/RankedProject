package net.rankedproject.velocity.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.server.ServerInfo;
import java.net.InetSocketAddress;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.data.Server;
import net.rankedproject.velocity.VelocityProxy;
import net.rankedproject.velocity.server.data.LoadedServer;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class ServerInstanceFacade {

    private final VelocityProxy plugin;
    private final ServerTracker serverTracker;

    /**
     * Creates and registers a new server instance in the proxy and tracker.
     * A new {@link LoadedServer} is constructed from the provided identifier, type, and address.
     *
     * @param identifier the unique server identifier
     * @param serverType the logical type of the server
     * @param address    the network address of the server
     * @return the created {@link LoadedServer} instance
     */
    public @NotNull LoadedServer createServer(
            final @NotNull String identifier,
            final @NotNull ServerType serverType,
            final @NotNull InetSocketAddress address
    ) {
        var server = new Server(identifier, serverType, address);
        var proxyServerInfo = new ServerInfo(identifier, address);

        var loadedServer = LoadedServer.builder()
                .server(server)
                .serverInfo(proxyServerInfo)
                .build();

        this.serverTracker.track(loadedServer);
        this.plugin.getProxyServer().registerServer(proxyServerInfo);

        return loadedServer;
    }

    /**
     * Registers an already loaded server into the proxy.
     *
     * @param loadedServer the server instance to register
     */
    public void loadServer(final @NotNull LoadedServer loadedServer) {
        var serverInfo = loadedServer.getServerInfo();
        this.plugin.getProxyServer().registerServer(serverInfo);
    }

    /**
     * Applies an update operation to a tracked server.
     * If the server does not exist, the operation is ignored.
     *
     * @param identifier           the identifier of the server to update
     * @param updateServerConsumer the modification to apply to the server
     */
    public void updateServer(
            final @NotNull String identifier,
            final @NotNull Consumer<LoadedServer> updateServerConsumer
    ) {
        var loadedServer = this.serverTracker.get(identifier);
        if (loadedServer == null) {
            return;
        }

        updateServerConsumer.accept(loadedServer);
        this.serverTracker.track(loadedServer);
    }

    /**
     * Removes a server from both the proxy registry and the internal tracker.
     * If the server does not exist, the call is ignored.
     *
     * @param identifier the identifier of the server to remove
     */
    public void removeServer(final @NotNull String identifier) {
        var loadedServer = this.serverTracker.get(identifier);
        if (loadedServer == null) {
            return;
        }

        var proxyServer = this.plugin.getProxyServer();
        var proxyServerInfo = loadedServer.getServerInfo();

        proxyServer.unregisterServer(proxyServerInfo);
        this.serverTracker.untrack(loadedServer);
    }
}
