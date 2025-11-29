package net.rankedproject.velocity.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
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
public class ServerHealthCheckTask implements Runnable {

    private static final String SERVER_UNTRACKED = "Untracked server %s because it didn't send a heartbeat response";

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final ServerTracker serverTracker;
    private final Logger logger;

    @NotNull
    public static ServerHealthCheckTask runTask(@NotNull Injector injector) {
        var healthCheckTask = injector.getInstance(ServerHealthCheckTask.class);
        SCHEDULER.scheduleAtFixedRate(healthCheckTask, 1, 1, TimeUnit.SECONDS);

        return healthCheckTask;
    }

    @Override
    public void run() {
        var servers = new ArrayList<>(serverTracker.getAllServers());

        for (var loadedServer : servers) {
            long elapsedMs = System.currentTimeMillis() - loadedServer.lastPingMs();
            logger.log(Level.FINE, () -> "Loaded server: " + loadedServer);

            logger.info(String.valueOf(Duration.ofMillis(elapsedMs).toSeconds()));
            if (elapsedMs > TIMEOUT.toMillis()) {
                serverTracker.untrack(loadedServer);
                logger.log(Level.INFO, String.format(SERVER_UNTRACKED, loadedServer));
            }
        }
    }
}