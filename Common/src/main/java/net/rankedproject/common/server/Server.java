package net.rankedproject.common.server;

import net.rankedproject.common.util.ServerType;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;

public record Server (
        UUID serverUUID,
        ServerType serverType,
        InetSocketAddress address,
        Set<UUID> players,
        int maxPlayers
) {

    @NotNull
    public String getUniqueIdentifier() {
        return serverType.getIdentifier() + "_" + serverUUID;
    }
}
