package net.rankedproject.common.packet;

import com.google.protobuf.GeneratedMessage;
import io.nats.client.Message;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for incoming protobuf-based packets (messages).
 *
 * <p>Implementations declare the subject to listen on and the protobuf type to
 * expect. When a raw {@link Message} arrives (for example from NATS), the
 * default {@link #onPacket(Message)} will parse the bytes using the static
 * {@code parseFrom(byte[])} method of the declared protobuf class and forward
 * the typed instance to {@link #onPacket(GeneratedMessage)}.
 */
public interface PacketListener<T extends GeneratedMessage> {

    /**
     * Handle a parsed protobuf packet.
     *
     * <p>This method receives a fully-parsed protobuf instance of type {@code T}.
     * Implementations should process the packet content here. Avoid long blocking
     * operations; use an executor if you need asynchronous or CPU-heavy work.
     *
     * <p>Exceptions thrown here should be handled by the implementor. If not
     * handled, they may propagate to the messaging dispatcher.
     *
     * @param packet the parsed protobuf message
     */
    void onPacket(final @NotNull T packet);

    /**
     * Returns metadata about this packet listener.
     *
     * <p>The metadata typically includes the subject/topic this listener subscribes to
     * and the protobuf type it expects. This information is used by the messaging
     * system to route messages correctly.
     *
     * @return metadata describing the listener's subscription and packet type
     */
    @NotNull PacketListenerMetadata<T> metadata();

    /**
     * Default handler for raw transport messages.
     *
     * <p>This method parses raw {@link Message#getData()} into the protobuf type
     * declared by {@link #metadata().packetType()} and delegates to {@link #onPacket(T)}.
     * Use this default unless you need custom raw access (for headers, reply
     * handling, etc).
     *
     * @param message raw transport message containing protobuf bytes
     */
    default void onPacket(final @NotNull Message message) {
        T parsedPacket = parsePacketDataFromMessage(message);
        onPacket(parsedPacket);
    }

    /**
     * Parses protobuf bytes from {@link Message}.
     *
     * <p>Uses reflection to call the static {@code parseFrom(byte[])} method on
     * the class returned by {@link #metadata().packetType()}. Any exception during
     * reflection or parsing will be thrown (currently via {@link lombok.SneakyThrows}).
     *
     * @param message the transport message carrying protobuf bytes
     * @return parsed protobuf instance of type {@code T}
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    private T parsePacketDataFromMessage(final @NotNull Message message) {
        var metadata = metadata();

        var classType = metadata.packetType();
        var parseMethod = classType.getMethod("parseFrom", byte[].class);

        return (T) parseMethod.invoke(null, (Object) message.getData());
    }
}
