package net.rankedproject.skywars;

import net.rankedproject.common.rest.impl.RankedPlayerRestClient;
import net.rankedproject.game.GamePlugin;
import net.rankedproject.game.finder.GameFinder;
import net.rankedproject.game.finder.RandomGameFinder;
import net.rankedproject.skywars.factory.RankedSkywarsGameFactory;
import net.rankedproject.spigot.server.RankedServer;
import net.rankedproject.spigot.server.RankedServerBuilder;
import org.jetbrains.annotations.NotNull;

public class RankedGamePlugin extends GamePlugin {

    @NotNull
    @Override
    protected RankedServer rankedServer(@NotNull RankedServerBuilder builder) {
        return builder.setName("RankedSkywars")
                .addRequiredPlayerData(RankedPlayerRestClient.class)
                .build();
    }

    @Override
    public GameFinder<?> get() {
        return new RandomGameFinder<>(injector, injector.getInstance(RankedSkywarsGameFactory.class));
    }
}