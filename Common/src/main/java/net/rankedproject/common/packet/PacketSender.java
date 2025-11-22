package net.rankedproject.common.packet;

import com.google.protobuf.GeneratedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * packetSender.send("velocity-1", packet)
 *      .await(CorePacket.CanPlayerRemoveIsland.class)
 *      .thenAccept(packet -> {
 *
 *      })
 *
 * packetSender.builder("velocity-1", packet)
 *      .await(CorePacket.CanPlayerRemoveIsland.class, Duration.ofMinutes(10))
 *      .send()
 *      .thenAccept(packet -> {
 *
 *      })
 *
 * packetSender.send(packet, "velocity-1")
 *      .thenAccept(packet -> {
 *
 *      })
 */
public interface PacketSender {

    <T extends GeneratedMessage, U extends GeneratedMessage> CompletableFuture<U> send(
            @NotNull PacketSendingData<T, U> packetSendingData
    );

    default <T extends GeneratedMessage, U extends GeneratedMessage> PacketSendingData.Builder<T, U> builder(
            @NotNull String subject,
            @NotNull T packet
    ) {
        return PacketSendingData.<T, U>builder(this)
                .subject(subject)
                .sendingPacket(packet);
    }
}
