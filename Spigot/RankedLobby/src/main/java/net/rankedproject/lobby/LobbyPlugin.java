package net.rankedproject.lobby;

import net.rankedproject.common.rest.impl.RankedPlayerRestClient;
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
                .addSpawn(new LobbySpawn(this))
                .addRegistrar(new SpawnWorldRegistrar(this))
                .addRequiredPlayerData(RankedPlayerRestClient.class)
                .build();
    }
}
