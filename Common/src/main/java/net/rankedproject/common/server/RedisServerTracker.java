package net.rankedproject.common.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.rankedproject.common.instantiator.impl.RedisInstantiator;
import net.rankedproject.common.util.ServerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.redisson.api.RMap;

import java.util.Collection;

public class RedisServerTracker implements ServerTracker {

    private final RMap<String, Server> servers;

    @Inject
    public RedisServerTracker(Injector injector) {
        var redis = injector.getInstance(RedisInstantiator.class).get();
        this.servers = redis.getMap("server-instances");
    }

    @Override
    public void track(@NotNull Server server) {
        this.servers.fastPut(server.getUniqueIdentifier(), server);
    }

    @Override
    public void untrack(@NotNull Server server) {
        this.servers.fastPut(server.getUniqueIdentifier(), server);
    }

    @NotNull
    @Override
    @UnmodifiableView
    public Collection<? extends Server> getAllServers() {
        return servers.values();
    }

    @NotNull
    @Override
    @UnmodifiableView
    public Collection<? extends Server> getAllServersByType(@NotNull ServerType serverType) {
        return servers.values();
    }
}
