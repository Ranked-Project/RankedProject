package net.rankedproject.common.instantiator.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.rankedproject.common.instantiator.Instantiator;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NatsInstantiator implements Instantiator<Connection> {

    @NotNull
    @Override
    @SneakyThrows
    public Connection initInternally() {
        return Nats.connect();
    }

    /**
     * Should support:
     * - Callbacks on reply back
     * - Timeouts
     * - Send to specific subjects
     * - Listen to specific subjects
     * - Support automatic packet conversion from/to byte array with protobuf
     *
     * - send method returs CF?
     *
     * Proxies: Velocity-1, Velocity-2
     *
     * Velocity-1: List<SpigotServer> servers = Lobby-1, Lobby-2, Lobby-3, Lobby-4
     * Velocity-2: List<SpigotServer> servers = Lobby-1, Lobby-2, Lobby-3, Lobby-4
     *
     * Velocity servers manage players connections
     * Players connected to 1 velocity server
     *
     * record NetworkPlayer(String spigotServerIdentifier, String velocityServerIdentifier) {
     *
     * }
     *
     * Send from one spigot server to another would be:
     *
     * var packet = CorePacket.SendPlayerToServer.newBuilder()
     *                  .serverName("skywars-server-1")
     *                  .playerUUID(playerUUID)
     *                  .build();
     *
     * packetSender.packet(packet)
     *             .subject("velocity-1")
     *             .packet(packet)
     *             .response(response -> {
     *
     *             })
     *
     *
     *
     *
     *
     * injector.getInstance(PacketSender.class)
     *
     *      .to("skywars-server-1")
     *      .packet(packet);
     *
     * injector.getInstance(PacketSender.class)
     *      .to("skywars-server-1")
     *      .packet(packet);
     */
}
