package net.rankedproject.velocity.server.picker;

import java.util.UUID;
import lombok.Getter;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a strategy for selecting an appropriate backend server instance.
 *
 * <p>This abstract class defines the API for all server–picking mechanisms.
 * A {@link ServerPicker} is responsible for choosing a {@link LoadedServer}
 * for a given player and {@link ServerType}. Implementations may use
 * different selection algorithms — for example:
 *
 * <ul>
 *   <li><b>LeastPlayers</b> – selects the server with the lowest online count</li>
 *   <li><b>MostPlayers</b> – prefers servers with the most remaining slots</li>
 *   <li>Any custom algorithm provided by plugin developers</li>
 * </ul>
 *
 * <p>All pickers operate on data supplied by {@link ServerTracker}, which keeps
 * track of currently loaded/connected backend servers.
 *
 * <p>This abstraction allows developers to easily plug in their own
 * server-selection logic by extending this class and returning a matching
 * {@link ServerPickerType}.
 */
@Getter
public abstract class ServerPicker {

    private final ServerTracker serverTracker;

    protected ServerPicker(final ServerTracker serverTracker) {
        this.serverTracker = serverTracker;
    }

    /**
     * Selects a backend server for the given player and server type.
     *
     * <p>This method implements the core logic of the picker. It must return a
     * non-null {@link LoadedServer} that matches the provided {@link ServerType}.
     *
     * @param playerUUID the unique identifier of the player requesting the server
     * @param serverType the type of server being requested (e.g., LOBBY, GAME, etc.)
     * @return the selected server instance
     */
    public abstract @NotNull LoadedServer getServer(@NotNull UUID playerUUID, @NotNull ServerType serverType);

    /**
     * Returns the type representing this selection strategy.
     *
     * <p>This is used by the API to identify which picker is being used
     * (e.g., LEAST_PLAYERS, MOST_PLAYERS, etc.).
     *
     * @return the enum value representing this server picker strategy
     */
    public abstract @NotNull ServerPickerType getServerPickerType();
}
