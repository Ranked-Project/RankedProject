package net.rankedproject.velocity.server.picker;

import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import net.rankedproject.velocity.server.tracker.ServerTracker;

import java.util.UUID;

public abstract class ServerPicker {

    protected final ServerTracker serverTracker;

    protected ServerPicker(ServerTracker serverTracker) {
        this.serverTracker = serverTracker;
    }

    public abstract LoadedServer getServer(UUID playerUUID, ServerType serverType);

    public abstract ServerPickerType getServerPickerType();
}
