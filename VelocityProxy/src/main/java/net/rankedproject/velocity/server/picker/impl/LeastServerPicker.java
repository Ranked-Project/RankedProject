package net.rankedproject.velocity.server.picker.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.velocity.server.data.LoadedServer;
import net.rankedproject.velocity.server.picker.ServerPicker;
import net.rankedproject.velocity.server.tracker.ServerTracker;

import java.util.Comparator;
import java.util.UUID;

@Singleton
public final class LeastServerPicker extends ServerPicker {

    @Inject
    public LeastServerPicker(ServerTracker serverTracker) {
        super(serverTracker);
    }

    @Override
    public LoadedServer getServer(UUID playerUUID, ServerType serverType) {
        return serverTracker.getAllServersByType(serverType)
                .stream()
                .min(Comparator.comparingInt(LoadedServer::getOnlinePlayers))
                .orElseThrow();
    }

    @Override
    public ServerPickerType getServerPickerType() {
        return ServerPickerType.LEAST;
    }
}