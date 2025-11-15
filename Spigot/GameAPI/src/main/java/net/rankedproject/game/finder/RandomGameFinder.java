package net.rankedproject.game.finder;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.game.factory.GameFactory;
import net.rankedproject.game.tracker.GameTracker;
import net.rankedproject.gameapi.Game;
import net.rankedproject.gameapi.metadata.GameMetadata;
import net.rankedproject.spigot.CommonPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RandomGameFinder<G extends Game, M extends GameMetadata> implements GameFinder<G> {

    private final CommonPlugin plugin;

    private final GameTracker gameTracker;
    private final GameFactory<G, M> gameFactory;

    public RandomGameFinder(@NotNull Injector injector, @NotNull GameFactory<G, M> gameFactory) {
        this.gameFactory = gameFactory;

        this.plugin = injector.getInstance(CommonPlugin.class);
        this.gameTracker = injector.getInstance(GameTracker.class);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<G> find(@NotNull UUID playerUUID) {
        var availableGame = gameTracker.getGames().stream()
                .sorted(Comparator.<Game>comparingLong(game -> game.getPlayerTracker().getPlayers().size()).reversed())
                .filter(game -> !game.getPlayerTracker().getPlayers().contains(playerUUID))
                .filter(game -> game.getStateContext().getCurrentState().isState(game.getInitState().getClass()))
                .findFirst();

        return availableGame.map(game -> CompletableFuture.completedFuture((G) game))
                .orElseGet(() -> gameFactory.create(plugin, findRandomGameIdentifier(plugin.getInjector())))
                .thenApply(game -> {
                    if (!game.isStarted()) {
                        game.start();
                    }
                    return game;
                });
    }
}