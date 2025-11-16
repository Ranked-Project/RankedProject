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

    @Nullable
    public Game getGameByPlayer(@NotNull UUID playerUUID) {
        return games.stream()
                .filter(game -> game.getPlayerTracker().getPlayers().contains(playerUUID))
                .findFirst()
                .orElse(null);
    }

    public void track(@NotNull Game game) {
        this.games.add(game);
    }

    public void untrack(@NotNull Game game) {
        this.games.remove(game);
    }

    @NotNull
    @UnmodifiableView
    public List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }
}