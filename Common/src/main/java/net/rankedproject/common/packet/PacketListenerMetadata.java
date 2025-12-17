package net.rankedproject.common.packet;

import com.google.common.base.Preconditions;
import com.google.protobuf.GeneratedMessage;
import org.jetbrains.annotations.NotNull;

public record PacketListenerMetadata<T extends GeneratedMessage>(
        Class<? extends T> packetType,
        String subject
) {

    /**
     * Creates a new builder for PacketListenerMetadata, which keep essential information for listener registration
     *
     * @return Builder for creating new PacketListenerMetadata
     */
    public static <T extends GeneratedMessage> PacketListenerMetadata.Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T extends GeneratedMessage> {

        private Class<? extends T> packetType;
        private String subject;

        private Builder() {

        }

        /**
         * Returns builder with the subject/topic this listener handles.
         *
         * <p>The subject is used by the messaging layer (e.g. NATS) to route
         * messages to this listener. Use clear, namespaced subjects to avoid
         * accidental collisions (for example: {@code "service.component.event"}).
         *
         * @return Builder with the subject string to subscribe to
         */
        public @NotNull Builder<T> packetType(final @NotNull Class<? extends T> packetType) {
            this.packetType = packetType;
            return this;
        }

        /**
         * Returns builder with the protobuf packet class this listener expects.
         *
         * <p>This class is used to parse incoming raw bytes into a typed protobuf
         * instance via its static {@code parseFrom(byte[])} method.
         *
         * @return Builder with the protobuf class to parse incoming messages into
         */
        public @NotNull Builder<T> subject(final @NotNull String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Builds new PacketListenerMetadata object
         *
         * @return PacketListenerMetadata keeping essential information to register listener
         */
        public @NotNull PacketListenerMetadata<T> build() {
            Preconditions.checkNotNull(packetType, "packetType can't be null");
            Preconditions.checkNotNull(subject, "subject can't be null");

            return new PacketListenerMetadata<>(packetType, subject);
        }
    }
}
