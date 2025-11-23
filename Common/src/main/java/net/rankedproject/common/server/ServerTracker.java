package net.rankedproject.common.server;

import net.rankedproject.common.util.ServerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface ServerTracker {

    void track(@NotNull Server server);

    void untrack(@NotNull Server server);

    @NotNull
    @UnmodifiableView
    Collection<? extends Server> getAllServers();

    @NotNull
    @UnmodifiableView
    Collection<? extends Server> getAllServersByType(@NotNull ServerType serverType);
}
