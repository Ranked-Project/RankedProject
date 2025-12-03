package net.rankedproject.spigot.server.heartbeat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerNetworkGateway;
import net.rankedproject.spigot.CommonPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerHeartbeat implements Runnable {

    private static final String SERVER_PING = "Pinging (%s)";
    private static int count = 0;

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    private final ServerNetworkGateway serverNetworkGateway;
    private final CommonPlugin plugin;
    private final Logger logger;

    public static void runTask(@NotNull Injector injector) {
        var serverPingTask = injector.getInstance(ServerHeartbeat.class);
        SCHEDULER.scheduleAtFixedRate(serverPingTask, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        var rankedServer = plugin.getRankedServer();

        var identifier = rankedServer.identifier();
        var serverType = rankedServer.serverType();

        var ip = Bukkit.getIp();
        var port = Bukkit.getPort();
        var maxPlayers = Bukkit.getMaxPlayers();

        var inetSocketAddress = new InetSocketAddress(ip, port);
        serverNetworkGateway
                .sendPingSpigotServerPacket(identifier, serverType, inetSocketAddress, maxPlayers)
                .thenRun(() -> {
                    count++;
                    logger.info(SERVER_PING.formatted(count));
                });
    }
}