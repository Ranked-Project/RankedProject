package net.rankedproject.velocity.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.impl.RedisInstantiator;
import net.rankedproject.common.server.Server;
import net.rankedproject.common.util.ServerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.redisson.api.RMap;

import java.util.Collection;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class ServerTracker {

    private final RMap<UUID, LoadedServer> loadedServers;

    @Inject
    public ServerTracker(Injector injector) {
        var redis = injector.getInstance(RedisInstantiator.class).get();
        this.loadedServers = redis.getMap("server-instances");
    }

    public void track(@NotNull LoadedServer loadedServer) {
        this.loadedServers.fastPut(loadedServer.server().getServerUUID(), loadedServer);
    }

    public void untrack(@NotNull LoadedServer loadedServer) {
        this.loadedServers.fastRemove(loadedServer.server().getServerUUID());
    }

    public LoadedServer get(@NotNull UUID serverUUID) {
        return this.loadedServers.get(serverUUID);
    }

    @NotNull
    @UnmodifiableView
    public Collection<? extends LoadedServer> getAllServers() {
        return loadedServers.values();
    }

    @NotNull
    @UnmodifiableView
    public Collection<? extends LoadedServer> getAllServersByType(@NotNull ServerType serverType) {
        return loadedServers.values()
                .stream()
                .filter(loadedServer -> loadedServer.server().getServerType().equals(serverType))
                .toList();
    }
}