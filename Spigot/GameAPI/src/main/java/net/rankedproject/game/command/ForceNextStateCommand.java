package net.rankedproject.game.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.game.tracker.GameTracker;
import net.rankedproject.spigot.command.RankedCommand;
import net.rankedproject.spigot.util.ComponentUtil;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ForceNextStateCommand implements RankedCommand {

    private final GameTracker gameTracker;

    @NotNull
    @Override
    public Command<PlayerSource> command(@NotNull PaperCommandManager<Source> commandManager) {
        return commandManager.commandBuilder("forcenextstate")
                .senderType(PlayerSource.class)
                .handler(executor -> {
                    var player = executor.sender().source();
                    var game = gameTracker.getGameByPlayer(player.getUniqueId());
                    if (game == null) {
                        player.sendMessage(ComponentUtil.deserialize("<red>You are not in a game!"));
                        return;
                    }

                    var stateContext = game.getStateContext();
                    stateContext.switchNextState();

                    player.sendMessage(ComponentUtil.deserialize("<green>Forcefully switched the game's state to the next one"));
                })
                .build();
    }
}
