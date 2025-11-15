package net.rankedproject.game.finder;

import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.gameapi.Game;
import net.rankedproject.gameapi.config.MapInfoConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@ImplementedBy(RandomGameFinder.class)
public interface GameFinder<G extends Game> {

    /**
     * Finds existing waiting to start game or creates a new one
     *
     * @param playerUUID UUID of the player looking for a game
     * @return Game
     */
    @NotNull
    CompletableFuture<G> find(@NotNull UUID playerUUID);

    /**
     * Finds random game's identifier
     *
     * @return Game identifier
     */
    @NotNull
    default String findRandomGameIdentifier(Injector injector) {
        var ids = ConfigProvider.get(MapInfoConfig.class, injector)
                .path("games-active-ids")
                .getAsList(String.class);

        var randomIndex = ThreadLocalRandom.current().nextInt(0, ids.size());
        return ids.get(randomIndex);
    }
}
