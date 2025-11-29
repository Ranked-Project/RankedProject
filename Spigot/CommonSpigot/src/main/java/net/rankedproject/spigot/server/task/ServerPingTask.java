package net.rankedproject.spigot.server.task;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.packet.sender.PacketSender;
import net.rankedproject.common.packet.sender.PacketSenderImpl;
import net.rankedproject.common.packet.sender.data.PacketSendingData;
import net.rankedproject.proto.SendPlayerToServer;
import net.rankedproject.proto.SpigotServerPing;
import net.rankedproject.spigot.CommonPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerPingTask implements Runnable {

    private static final String SERVER_PING = "Pinging (%s)";
    private static int count = 0;

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    private final CommonPlugin plugin;
    private final Logger logger;

    @NotNull
    public static ServerPingTask runTask(@NotNull Injector injector) {
        var serverPingTask = injector.getInstance(ServerPingTask.class);
        SCHEDULER.scheduleAtFixedRate(serverPingTask, 1, 1, TimeUnit.SECONDS);

        injector.getInstance(Logger.class).info("Server ping task started");
        return serverPingTask;
    }

    @Override
    public void run() {
        var packet = SpigotServerPing.newBuilder()
                .setServerUuid(plugin.getServerUUID().toString())
                .build();

        var packetSender = plugin.getInjector().getInstance(PacketSenderImpl.class);
        packetSender.builder("spigot.server.ping", packet).send();

        count++;
        logger.info(SERVER_PING.formatted(count));
    }
}