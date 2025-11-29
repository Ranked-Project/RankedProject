package net.rankedproject.common.server.network;

import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.packet.sender.PacketSender;
import net.rankedproject.proto.SendPlayerToServer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class NetworkPlayer {

    private final UUID playerUUID;
    private String proxyIdentifier;

    public void sendPlayerToServer(@NotNull String serverIdentifier, @NotNull Injector injector) {
        var packet = SendPlayerToServer.newBuilder()
                .setPlayerUuid(playerUUID.toString())
                .setServerIdentifier(serverIdentifier)
                .build();

        var packetSender = injector.getInstance(PacketSender.class);
        packetSender.builder("server.proxy." + proxyIdentifier, packet).send();
    }
}