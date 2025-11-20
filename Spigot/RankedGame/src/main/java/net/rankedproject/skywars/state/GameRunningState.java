package net.rankedproject.skywars.state;

import net.rankedproject.gameapi.state.GameState;
import net.rankedproject.gameapi.state.GameStateBehavior;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class GameRunningState implements GameState {

    private static final Duration DURATION = Duration.ofMinutes(1).plus(Duration.ofSeconds(30));

    @NotNull
    @Override
    public GameStateBehavior behavior() {
        return GameStateBehavior.builder()
                .duration(DURATION)
                .nextState(new GameRefillState())
                .build();
    }
}