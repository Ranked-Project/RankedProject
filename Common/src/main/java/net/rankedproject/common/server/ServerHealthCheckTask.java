package net.rankedproject.common.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.packet.PacketSender;
import net.rankedproject.common.packet.PacketSendingData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerHealthCheckTask implements Runnable {

    private static final String SERVER_UNTRACKED = "Untracked server %s because it didn't send a heartbeat response";

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private final ServerTracker serverTracker;
    private final PacketSender packetSender;
    private final Logger logger;

    @NotNull
    public static ServerHealthCheckTask runTask(@NotNull Injector injector) {
        var healthCheckTask = injector.getInstance(ServerHealthCheckTask.class);
        SCHEDULER.schedule(healthCheckTask, 3, TimeUnit.SECONDS);

        return healthCheckTask;
    }

    @Override
    public void run() {
        for (var server : serverTracker.getAllServers()) {
            var serverIdentifier = server.getUniqueIdentifier();

            PacketSendingData.builder(packetSender)
                    .subject("server." + serverIdentifier)
                    .sendAwaiting(TIMEOUT)
                    .whenComplete((_, throwable) -> {
                        if (throwable == null) {
                            return;
                        }

                        serverTracker.untrack(server);
                        logger.info(SERVER_UNTRACKED.formatted(server.getServerUUID()));
                    });
        }
    }
}