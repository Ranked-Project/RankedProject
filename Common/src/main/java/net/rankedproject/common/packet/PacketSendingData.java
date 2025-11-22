package net.rankedproject.common.packet;

import com.google.protobuf.GeneratedMessage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public record PacketSendingData<T extends GeneratedMessage, U extends GeneratedMessage>(
        String subject,
        T sendingPacket,
        Class<U> awaitingPacket
) {

    public static <T extends GeneratedMessage, U extends GeneratedMessage> PacketSendingData.Builder<T, U> builder(
            @NotNull PacketSender packetSender
    ) {
        return new Builder<>(packetSender);
    }

    @RequiredArgsConstructor
    public static class Builder<T extends GeneratedMessage, U extends GeneratedMessage> {

        private String subject;
        private T sendingPacket;

        private Duration timeout;
        private Class<U> awaitingPacket;

        private final PacketSender packetSender;

        public Builder(PacketSender packetSender, Class<U> awaitingPacket) {
            this.packetSender = packetSender;
            this.awaitingPacket = awaitingPacket;
        }

        public Builder<T, U> subject(@NotNull String subject) {
            this.subject = subject;
            return this;
        }

        public Builder<T, U> sendingPacket(@NotNull T sendingPacket) {
            this.sendingPacket = sendingPacket;
            return this;
        }

        public <V extends U> Builder<T, V> awaitingPacket(@NotNull Class<V> awaitingPacket) {
            return new Builder<T, V>(packetSender, awaitingPacket)
                    .sendingPacket(sendingPacket)
                    .subject(subject);
        }

        public CompletableFuture<U> send() {
            return packetSender.send(build());
        }

        public CompletableFuture<U> send(@NotNull Duration timeout) {
            this.timeout = timeout;
            return packetSender.send(build());
        }

        public PacketSendingData<T, U> build() {
            return new PacketSendingData<>(subject, sendingPacket, awaitingPacket);
        }
    }
}
