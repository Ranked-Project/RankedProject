package net.rankedproject.velocity.server.data;

import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Builder;
import lombok.Data;
import net.rankedproject.common.network.server.data.Server;

@Data
@Builder
public class LoadedServer {

    private final Server server;
    private final ServerInfo serverInfo;

    @Builder.Default
    private long lastPingMs = System.currentTimeMillis();

    @Builder.Default
    private int onlinePlayers = 0;
}
