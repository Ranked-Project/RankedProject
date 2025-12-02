package net.rankedproject.common.network.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.network.NetworkGateway;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.common.packet.sender.PacketSenderImpl;
import net.rankedproject.common.packet.sender.data.PacketSendingData;
import net.rankedproject.proto.PlayerSendToServer;
import net.rankedproject.proto.PlayerSendToServerByPicker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class PlayerNetworkGateway implements NetworkGateway {

    private final PacketSenderImpl packetSender;

    public CompletableFuture<?> sendPlayerToServerPacket(
            @NotNull UUID playerUUID,
            @NotNull String identifier
    ) {
        var generatedMessage = PlayerSendToServer.newBuilder()
                .setPlayerUuid(playerUUID.toString())
                .setServerIdentifier(identifier)
                .build();

        var packet = PacketSendingData.builder(packetSender)
                .sendingPacket(generatedMessage)
                .subject("player.server.send.specific")
                .build();

        return send(packet, packetSender);
    }

    public CompletableFuture<?> sendPlayerToServerPacket(
            @NotNull UUID playerUUID,
            @NotNull ServerType serverType,
            @NotNull ServerPickerType serverPickerType
    ) {
        var generatedMessage = PlayerSendToServerByPicker.newBuilder()
                .setPlayerUuid(playerUUID.toString())
                .setServerType(serverType.toString())
                .setServerPickerType(serverPickerType.toString())
                .build();

        var packet = PacketSendingData.builder(packetSender)
                .sendingPacket(generatedMessage)
                .subject("player.server.send.picker")
                .build();

        return send(packet, packetSender);
    }
}