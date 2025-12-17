package net.rankedproject.game.tracker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.gameapi.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameTracker {

    private final List<Game> games = new ArrayList<>();

    public @Nullable Game getGameByPlayer(final @NotNull UUID playerUUID) {
        return games.stream()
                .filter(game -> game.getPlayerTracker().getPlayers().contains(playerUUID))
                .findFirst()
                .orElse(null);
    }

    public void track(final @NotNull Game game) {
        this.games.add(game);
    }

    public void untrack(final @NotNull Game game) {
        this.games.remove(game);
    }

    @UnmodifiableView
    public @NotNull List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }
}
