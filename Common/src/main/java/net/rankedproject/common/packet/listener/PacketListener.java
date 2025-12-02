package net.rankedproject.common.packet.listener;

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
 *
 * @param <T> protobuf message type this listener processes
 */
public interface PacketListener<T extends GeneratedMessage> {

    /**
     * Returns the protobuf packet class this listener expects.
     *
     * <p>This class is used to parse incoming raw bytes into a typed protobuf
     * instance via its static {@code parseFrom(byte[])} method.
     *
     * @return the protobuf class to parse incoming messages into
     */
    @NotNull Class<T> getPacketType();

    /**
     * Returns the subject/topic this listener handles.
     *
     * <p>The subject is used by the messaging layer (e.g. NATS) to route
     * messages to this listener. Use clear, namespaced subjects to avoid
     * accidental collisions (for example: {@code "service.component.event"}).
     *
     * @return the subject string to subscribe to
     */
    @NotNull String getSubject();

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
    void onPacket(@NotNull T packet);

    /**
     * Default handler for raw transport messages.
     *
     * <p>This method parses raw {@link Message#getData()} into the protobuf type
     * declared by {@link #getPacketType()} and delegates to {@link #onPacket(T)}.
     * Use this default unless you need custom raw access (for headers, reply
     * handling, etc).
     *
     * @param message raw transport message containing protobuf bytes
     */
    default void onPacket(@NotNull Message message) {
        T parsedPacket = parsePacketDataFromMessage(message);
        onPacket(parsedPacket);
    }

    /**
     * Parses protobuf bytes from {@link Message}.
     *
     * <p>Uses reflection to call the static {@code parseFrom(byte[])} method on
     * the class returned by {@link #getPacketType()}. Any exception during
     * reflection or parsing will be thrown (currently via {@link lombok.SneakyThrows}).
     *
     * @param message the transport message carrying protobuf bytes
     * @return parsed protobuf instance of type {@code T}
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    private T parsePacketDataFromMessage(@NotNull Message message) {
        var classType = getPacketType();
        var parseMethod = classType.getMethod("parseFrom", byte[].class);

        return (T) parseMethod.invoke(null, (Object) message.getData());
    }
}
