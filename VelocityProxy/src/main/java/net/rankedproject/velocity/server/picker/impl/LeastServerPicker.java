package net.rankedproject.velocity.server.picker.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Comparator;
import java.util.UUID;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import net.rankedproject.velocity.server.picker.ServerPicker;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

@Singleton
public final class LeastServerPicker extends ServerPicker {

    /**
     * Creates a server picker prioritizing servers with the fewest players online.
     *
     * @param serverTracker tracker providing server information
     */
    @Inject
    public LeastServerPicker(final ServerTracker serverTracker) {
        super(serverTracker);
    }

    /**
     * Selects a server of the given type with the lowest number of online players.
     *
     * @param playerUUID the player requesting the server
     * @param serverType the server type to search for
     * @return the server with the fewest players online
     */
    @Override
    public @NotNull LoadedServer getServer(
            final @NotNull UUID playerUUID,
            final @NotNull ServerType serverType
    ) {
        return getServerTracker().getAllServersByType(serverType)
                .stream()
                .min(Comparator.comparingInt(LoadedServer::getOnlinePlayers))
                .orElseThrow();
    }

    @Override
    public @NotNull ServerPickerType getServerPickerType() {
        return ServerPickerType.LEAST;
    }
}
