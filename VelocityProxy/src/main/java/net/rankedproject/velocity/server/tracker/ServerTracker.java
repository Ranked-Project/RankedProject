package net.rankedproject.velocity.server.tracker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.impl.RedisInstantiator;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.redisson.api.RMap;

@Singleton
@RequiredArgsConstructor
public final class ServerTracker {

    private final RMap<String, LoadedServer> loadedServers;

    /**
     * Constructs a new {@link ServerTracker} and initializes the Redis-backed map
     * used to store and track {@link LoadedServer} instances.
     *
     * @param injector the Guice injector used to obtain the Redis instantiator
     */
    @Inject
    public ServerTracker(final @NotNull Injector injector) {
        var redis = injector.getInstance(RedisInstantiator.class).get();
        this.loadedServers = redis.getMap("server-instances");
    }

    /**
     * Updates a tracked server by applying the given consumer.
     * If the server does not exist, the update is ignored.
     *
     * @param serverIdentifier     the identifier of the server to update
     * @param serverUpdateConsumer the update logic to apply
     */
    public void update(
            final @NotNull String serverIdentifier,
            final @NotNull Consumer<LoadedServer> serverUpdateConsumer
    ) {
        var loadedServer = this.loadedServers.get(serverIdentifier);

        if (loadedServer != null) {
            serverUpdateConsumer.accept(loadedServer);
            this.track(loadedServer);
        }
    }

    /**
     * Tracks (creates or updates) a {@link LoadedServer} instance in Redis.
     *
     * @param loadedServer the server instance to store
     */
    public void track(final @NotNull LoadedServer loadedServer) {
        this.loadedServers.fastPut(loadedServer.getServer().getIdentifier(), loadedServer);
    }

    /**
     * Removes a {@link LoadedServer} instance from tracking.
     *
     * @param loadedServer the server instance to remove
     */
    public void untrack(final @NotNull LoadedServer loadedServer) {
        this.loadedServers.fastRemove(loadedServer.getServer().getIdentifier());
    }

    /**
     * Retrieves a tracked server by its identifier.
     *
     * @param identifier the server identifier
     * @return the matching {@link LoadedServer}, or {@code null} if not found
     */
    public LoadedServer get(final @NotNull String identifier) {
        return this.loadedServers.get(identifier);
    }

    /**
     * Returns an unmodifiable view of all tracked servers.
     *
     * @return all tracked {@link LoadedServer} instances
     */
    @UnmodifiableView
    public @NotNull Collection<LoadedServer> getAllServers() {
        return Collections.unmodifiableCollection(this.loadedServers.values());
    }

    /**
     * Returns all tracked servers of a specific {@link ServerType}.
     *
     * @param serverType the server type to filter by
     * @return all servers matching the given type
     */
    @UnmodifiableView
    public @NotNull Collection<LoadedServer> getAllServersByType(final @NotNull ServerType serverType) {
        return this.loadedServers.values()
                .stream()
                .filter(loadedServer -> loadedServer.getServer().getServerType().equals(serverType))
                .toList();
    }
}
