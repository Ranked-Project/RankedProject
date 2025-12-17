package net.rankedproject.common.packet;

import com.google.protobuf.GeneratedMessage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public record PacketSendingData<T extends GeneratedMessage, U extends GeneratedMessage>(
        String subject,
        T sendingPacket,
        Duration timeout,
        Class<U> awaitingPacket
) {

    public static <T extends GeneratedMessage, U extends GeneratedMessage> PacketSendingData.@NotNull Builder<T, U> builder(
            final @NotNull PacketSender packetSender
    ) {
        return new Builder<>(packetSender);
    }

    @RequiredArgsConstructor
    public static final class Builder<T extends GeneratedMessage, U extends GeneratedMessage> {

        private String subject;
        private T packet;

        private Class<U> awaitingPacket;
        private Duration timeout = Duration.ofMinutes(10);

        private final PacketSender packetSender;

        private Builder(final @NotNull PacketSender packetSender, final @NotNull Class<U> awaitingPacket) {
            this.packetSender = packetSender;
            this.awaitingPacket = awaitingPacket;
        }

        public @NotNull Builder<T, U> subject(final @NotNull String subject) {
            this.subject = subject;
            return this;
        }

        public @NotNull Builder<T, U> packet(final @NotNull T sendingPacket) {
            this.packet = sendingPacket;
            return this;
        }

        public <V extends U> @NotNull Builder<T, V> awaitingPacket(final @NotNull Class<V> awaitingPacket) {
            return new Builder<T, V>(packetSender, awaitingPacket)
                    .packet(packet)
                    .subject(subject);
        }

        public @NotNull CompletableFuture<Void> send() {
            return packetSender.send(build());
        }

        public @NotNull CompletableFuture<Void> send(final @NotNull Duration timeout) {
            this.timeout = timeout;
            return packetSender.send(build());
        }

        public @NotNull CompletableFuture<U> sendAwaiting() {
            return packetSender.sendAwaiting(build());
        }

        public @NotNull CompletableFuture<U> sendAwaiting(final @NotNull Duration timeout) {
            this.timeout = timeout;
            return packetSender.sendAwaiting(build());
        }

        public @NotNull PacketSendingData<T, U> build() {
            return new PacketSendingData<>(subject, packet, timeout, awaitingPacket);
        }
    }
}
