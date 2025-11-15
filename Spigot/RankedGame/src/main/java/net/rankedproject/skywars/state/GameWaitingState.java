package net.rankedproject.skywars.state;

import net.rankedproject.common.localization.Localization;
import net.rankedproject.gameapi.Game;
import net.rankedproject.gameapi.event.GameEventListenerData;
import net.rankedproject.gameapi.event.type.GamePlayerJoinEvent;
import net.rankedproject.gameapi.mechanic.impl.NoBreakBlockMechanic;
import net.rankedproject.gameapi.mechanic.impl.NoPlayerDamageMechanic;
import net.rankedproject.gameapi.state.GameState;
import net.rankedproject.gameapi.state.GameStateBehavior;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class GameWaitingState implements GameState {

    private static final String EVENT_ID = "playerjoinevent-event";
    private static final Duration DURATION = Duration.ofMinutes(2);

    @NotNull
    @Override
    public GameStateBehavior behavior() {
        return GameStateBehavior.builder()
                .startAction(gameStartAction())
                .duration(DURATION)
                .build();
    }

    @NotNull
    private Consumer<Game> gameStartAction() {
        return game -> {
            var mechanicContext = game.getMechanicContext();
            mechanicContext.enable(new NoPlayerDamageMechanic(game));
            mechanicContext.enable(new NoBreakBlockMechanic(game));

            var playerJoinEvent = GameEventListenerData.<GamePlayerJoinEvent>builder()
                    .create(EVENT_ID, GamePlayerJoinEvent.class)
                    .on(event -> {
                        var metadata = game.getMetadata();
                        var world = game.getWorldContext().getWorld();

                        var locations = metadata.getSpawnLocations();
                        var randomLocation = locations.get(ThreadLocalRandom.current().nextInt(locations.size()));
                        randomLocation.setWorld(world);

                        var player = event.getPlayer();
                        player.teleportAsync(randomLocation);

                        var localization = game.getPlugin().getInjector().getInstance(Localization.class);
                        localization.path("game-join", player.getUniqueId())
                                .placeholder("player", player.getName())
                                .placeholder("amount", game.getPlayerTracker().getPlayers().size())
                                .placeholder("total", 4)
                                .sendMessage("messages");
                    })
                    .build();

            var eventContext = game.getEventContext();
            eventContext.register(playerJoinEvent);
        };
    }
}