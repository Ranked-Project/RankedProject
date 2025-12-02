package net.rankedproject.common.network;

import com.google.protobuf.GeneratedMessage;
import net.rankedproject.common.packet.sender.PacketSender;
import net.rankedproject.common.packet.sender.data.PacketSendingData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface NetworkGateway {

    @NotNull
    default CompletableFuture<?> send(
            @NotNull PacketSendingData<?, ?> packet,
            @NotNull PacketSender packetSender
    ) {
        return packetSender.send(packet);
    }
}