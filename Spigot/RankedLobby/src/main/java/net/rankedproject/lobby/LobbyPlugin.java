package net.rankedproject.lobby;

import net.rankedproject.common.rest.impl.RankedPlayerRestClient;
import net.rankedproject.lobby.config.LobbyConfig;
import net.rankedproject.lobby.registrar.SpawnWorldRegistrar;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.server.RankedServer;
import net.rankedproject.spigot.server.RankedServerBuilder;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class LobbyPlugin extends CommonPlugin {

    @Override
    public void onLoad() {
        super.onLoad();
        saveDefaultConfig();
        // All onLoad hooks run before BetterModel builds its models/resource pack on enable.
        var betterModel = Objects.requireNonNull(getServer().getPluginManager().getPlugin("BetterModel"));
        var destination = betterModel.getDataFolder().toPath().resolve("models/ranked_bust.bbmodel");
        try (var source = Objects.requireNonNull(getResource("models/ranked_bust.bbmodel"))) {
            Files.createDirectories(destination.getParent());
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot install the bundled BetterModel NPC", exception);
        }
    }

    @NotNull
    @Override
    public RankedServer rankedServer() {
        return new RankedServerBuilder()
                .setName("Lobby")
                .addConfig(LobbyConfig.class)
                .addSpawn(new LobbySpawn(this))
                .addRegistrar(SpawnWorldRegistrar.class)
                .addRequiredPlayerData(RankedPlayerRestClient.class)
                .build();
    }
}
