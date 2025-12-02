package net.rankedproject.common.network.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.network.NetworkGateway;
import net.rankedproject.common.packet.sender.PacketSenderImpl;
import net.rankedproject.common.packet.sender.data.PacketSendingData;
import net.rankedproject.proto.SpigotServerConnect;
import net.rankedproject.proto.SpigotServerDisconnect;
import net.rankedproject.proto.SpigotServerPing;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class ServerNetworkGateway implements NetworkGateway {

    private final PacketSenderImpl packetSender;

    @NotNull
    public CompletableFuture<?> sendConnectSpigotServerPacket(
            @NotNull String identifier,
            @NotNull ServerType serverType,
            @NotNull InetSocketAddress address,
            int maxPlayers
    ) {
        var generatedMessage = SpigotServerConnect.newBuilder()
                .setServerIdentifier(identifier)
                .setServerType(serverType.toString())
                .setHostName(address.getHostString())
                .setPort(address.getPort())
                .setMaxPlayers(maxPlayers)
                .build();

        var packet = PacketSendingData.builder(packetSender)
                .sendingPacket(generatedMessage)
                .subject("server.spigot.connect")
                .build();

        return send(packet, packetSender);
    }

    @NotNull
    public CompletableFuture<?> sendPingSpigotServerPacket(
            @NotNull String identifier,
            @NotNull ServerType serverType,
            @NotNull InetSocketAddress address,
            int maxPlayers
    ) {
        var generatedMessage = SpigotServerPing.newBuilder()
                .setServerIdentifier(identifier)
                .setServerType(serverType.toString())
                .setHostName(address.getHostString())
                .setPort(address.getPort())
                .setMaxPlayers(maxPlayers)
                .build();

        var packet = PacketSendingData.builder(packetSender)
                .sendingPacket(generatedMessage)
                .subject("server.spigot.ping")
                .build();

        return send(packet, packetSender);
    }

    @NotNull
    public CompletableFuture<?> sendDisconnectSpigotServerPacket(@NotNull String identifier) {
        var generatedMessage = SpigotServerDisconnect.newBuilder()
                .setServerIdentifier(identifier)
                .build();

        var packet = PacketSendingData.builder(packetSender)
                .sendingPacket(generatedMessage)
                .subject("server.spigot.disconnect")
                .build();

        return send(packet, packetSender);
    }
}