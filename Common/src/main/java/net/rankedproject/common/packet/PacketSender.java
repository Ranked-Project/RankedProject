package net.rankedproject.common.packet;

import com.google.protobuf.GeneratedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface PacketSender {

    /**
     * Send a packet asynchronously without waiting for a response.
     *
     * <p>The returned {@link CompletableFuture} completes when the send operation
     * finishes. It may complete normally with an implementation-specific result
     * or complete exceptionally on error.
     *
     * @param <T> the protobuf message type being sent
     * @param packetSendingData data that contains subject, packet and optional metadata
     * @return a {@link CompletableFuture} that completes when the send finishes
     */
    <T extends GeneratedMessage> CompletableFuture<?> send(
            @NotNull PacketSendingData<T, ?> packetSendingData
    );

    /**
     * Send a packet and wait for a response of the given type.
     *
     * <p>The returned {@link CompletableFuture} completes with the response message
     * of type {@code U} or completes exceptionally on send error, timeout or
     * deserialization error. Exact timeout and error behavior are implementation-defined.
     *
     * @param <T> the protobuf message type being sent
     * @param <U> the protobuf message type expected in the response
     * @param packetSendingData data that specifies the request and expected response type
     * @return a {@link CompletableFuture} that completes with the response {@code U}
     */
    <T extends GeneratedMessage, U extends GeneratedMessage> CompletableFuture<U> sendAwaiting(
            @NotNull PacketSendingData<T, U> packetSendingData
    );

    /**
     * Create a prefilled {@link PacketSendingData.Builder} for this sender.
     *
     * <p>Builder is already bound to this {@code PacketSender} and prefilled with
     * the provided subject and packet. Use the builder to add metadata or overrides.
     *
     * @param <T> the protobuf message type being sent
     * @param <U> the protobuf message type expected in the response (use {@code Void} if none)
     * @param subject routing/identification subject for the message
     * @param packet the protobuf message instance to send
     * @return a preconfigured {@link PacketSendingData.Builder} ready for customization
     */
    default <T extends GeneratedMessage, U extends GeneratedMessage> PacketSendingData.Builder<T, U> builder(
            @NotNull String subject,
            @NotNull T packet
    ) {
        return PacketSendingData.<T, U>builder(this)
                .subject(subject)
                .sendingPacket(packet);
    }
}