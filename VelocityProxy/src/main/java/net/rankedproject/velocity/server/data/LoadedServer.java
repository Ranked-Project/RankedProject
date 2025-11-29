package net.rankedproject.velocity.server.data;

import net.rankedproject.common.server.Server;

public record LoadedServer(
        long lastPingMs,
        Server server
) {
}