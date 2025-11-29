package net.rankedproject.common.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.rankedproject.common.util.ServerType;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Server {

    private final UUID serverUUID;

    private final ServerType serverType;
    private final InetSocketAddress address;
    private final Set<UUID> players;

    private int maxPlayers = 100;

    @NotNull
    @Deprecated
    public String getUniqueIdentifier() {
        return serverType.getIdentifier() + "_" + serverUUID;
    }
}