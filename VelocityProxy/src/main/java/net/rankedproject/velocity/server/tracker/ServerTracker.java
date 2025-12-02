package net.rankedproject.velocity.server.tracker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.impl.RedisInstantiator;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.redisson.api.RMap;

import java.util.Collection;
import java.util.function.Consumer;

@Singleton
@RequiredArgsConstructor
public class ServerTracker {

    private final RMap<String, LoadedServer> loadedServers;

    @Inject
    public ServerTracker(Injector injector) {
        var redis = injector.getInstance(RedisInstantiator.class).get();
        this.loadedServers = redis.getMap("server-instances");
    }

    public void update(
            String serverIdentifier,
            @NotNull Consumer<LoadedServer> serverUpdateConsumer
    ) {
        var loadedServer = loadedServers.get(serverIdentifier);

        if (loadedServer != null) {
            serverUpdateConsumer.accept(loadedServer);
            track(loadedServer);
        }
    }

    public void track(@NotNull LoadedServer loadedServer) {
        this.loadedServers.fastPut(loadedServer.getServer().getIdentifier(), loadedServer);
    }

    public void untrack(@NotNull LoadedServer loadedServer) {
        this.loadedServers.fastRemove(loadedServer.getServer().getIdentifier());
    }

    public LoadedServer get(@NotNull String identifier) {
        return this.loadedServers.get(identifier);
    }

    @NotNull
    @UnmodifiableView
    public Collection<LoadedServer> getAllServers() {
        return loadedServers.values();
    }

    @NotNull
    @UnmodifiableView
    public Collection<LoadedServer> getAllServersByType(@NotNull ServerType serverType) {
        return loadedServers.values()
                .stream()
                .filter(loadedServer -> loadedServer.getServer().getServerType().equals(serverType))
                .toList();
    }
}
