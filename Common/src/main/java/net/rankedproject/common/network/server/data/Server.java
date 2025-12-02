package net.rankedproject.common.network.server.data;

import java.net.InetSocketAddress;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.rankedproject.common.network.server.ServerType;

@Getter
@Setter
@RequiredArgsConstructor
public class Server {

    private final String identifier;

    private final ServerType serverType;
    private final InetSocketAddress address;

    private int maxPlayers = 100;
}
