package net.rankedproject.common.server;

import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import net.rankedproject.CorePacket;
import net.rankedproject.common.packet.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class NetworkPlayer {

    private final UUID playerUUID;
    private String proxyIdentifier;

    public void sendPlayerToServer(@NotNull String serverIdentifier, @NotNull Injector injector) {
        var packet = CorePacket.SendPlayerToServer.newBuilder()
                .setPlayerUuid(playerUUID.toString())
                .setServerName(serverIdentifier)
                .build();

        var packetSender = injector.getInstance(PacketSender.class);
        packetSender.builder("server.proxy." + proxyIdentifier, packet).send();
    }
}