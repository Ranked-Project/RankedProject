package net.rankedproject.spigot.registrar.proxy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerNetworkGateway;
import net.rankedproject.common.registrar.AsyncRegistrar;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.server.heartbeat.ServerHeartbeat;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerProxyConnectRegistrar implements AsyncRegistrar {

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public CompletableFuture<?> registerAsync() {
        var rankedServer = plugin.getRankedServer();
        var injector = plugin.getInjector();

        var identifier = rankedServer.identifier();
        var serverType = rankedServer.serverType();

        var ip = Bukkit.getIp();
        int port = Bukkit.getPort();
        int maxPlayers = Bukkit.getMaxPlayers();

        var inetSocketAddress = new InetSocketAddress(ip, port);
        var serverNetworkGateway = injector.getInstance(ServerNetworkGateway.class);

        return serverNetworkGateway
                .sendConnectSpigotServerPacket(identifier, serverType, inetSocketAddress, maxPlayers)
                .thenRun(() -> ServerHeartbeat.runTask(injector));
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.FIRST;
    }
}