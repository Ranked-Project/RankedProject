package net.rankedproject.lobby;

import net.rankedproject.common.rest.impl.RankedPlayerRestClient;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.lobby.config.LobbyConfig;
import net.rankedproject.lobby.registrar.SpawnWorldRegistrar;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.server.RankedServer;
import net.rankedproject.spigot.server.RankedServerBuilder;
import org.jetbrains.annotations.NotNull;

public class LobbyPlugin extends CommonPlugin {

    @NotNull
    @Override
    public RankedServer rankedServer() {
        return new RankedServerBuilder()
                .setName("Lobby")
                .addConfig(LobbyConfig.class)
                .addSpawn(new LobbySpawn(this))
                .addRegistrar(SpawnWorldRegistrar.class)
                .addRequiredPlayerData(RankedPlayerRestClient.class)
                .setServerType(ServerType.RANKED_LOBBY)
                .build();
    }
}