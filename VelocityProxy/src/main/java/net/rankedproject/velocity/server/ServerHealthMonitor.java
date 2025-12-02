package net.rankedproject.velocity.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerHealthMonitor implements Runnable {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final ServerInstanceFacade serverInstanceFacade;
    private final ServerTracker serverTracker;
    private final Logger logger;

    public static void runTask(@NotNull Injector injector) {
        var healthCheckTask = injector.getInstance(ServerHealthMonitor.class);
        SCHEDULER.scheduleAtFixedRate(healthCheckTask, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        var servers = new ArrayList<>(serverTracker.getAllServers());

        for (var loadedServer : servers) {
            long elapsedMs = System.currentTimeMillis() - loadedServer.getLastPingMs();

            if (elapsedMs > TIMEOUT.toMillis()) {
                var identifier = loadedServer.getServer().getIdentifier();
                serverInstanceFacade.removeServer(identifier);
            }
        }
    }
}
