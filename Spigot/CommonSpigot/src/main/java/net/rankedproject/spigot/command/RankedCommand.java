package net.rankedproject.spigot.command;

import net.rankedproject.spigot.CommonPlugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;

public interface RankedCommand {

    @NotNull
    Command<? extends Source> command(@NotNull PaperCommandManager<Source> commandManager);

    default boolean shouldSkipCommandRegistration(@NotNull CommonPlugin plugin) {
        return plugin.getRankedServer()
                .ignoredCommands()
                .contains(getClass());
    }
}
