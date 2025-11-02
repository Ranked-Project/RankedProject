package net.rankedproject.skywars;

import net.rankedproject.common.rest.impl.RankedPlayerRestClient;
import net.rankedproject.gameapi.config.MapInfoConfig;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.server.RankedServer;
import net.rankedproject.spigot.server.RankedServerBuilder;
import org.jetbrains.annotations.NotNull;

public class GamePlugin extends CommonPlugin {

    @NotNull
    @Override
    public RankedServer rankedServer() {
        return new RankedServerBuilder()
                .setName("Lobby")
                .addConfig(MapInfoConfig.class)
                .addRequiredPlayerData(RankedPlayerRestClient.class)
                .build();
    }
}