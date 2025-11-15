package net.rankedproject.game;

import com.google.inject.Provider;
import net.rankedproject.game.finder.GameFinder;
import net.rankedproject.game.module.GamePluginModule;
import net.rankedproject.gameapi.config.MapInfoConfig;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.server.RankedServer;
import net.rankedproject.spigot.server.RankedServerBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class GamePlugin extends CommonPlugin implements Provider<GameFinder<?>> {

    @NotNull
    @Override
    protected final RankedServer rankedServer() {
        var builder = new RankedServerBuilder()
                .addConfig(MapInfoConfig.class)
                .addModule(new GamePluginModule(this));

        return rankedServer(builder);
    }

    @NotNull
    protected abstract RankedServer rankedServer(@NotNull RankedServerBuilder builder);
}