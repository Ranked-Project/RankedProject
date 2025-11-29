package net.rankedproject.common.packet.sender;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.protobuf.GeneratedMessage;
import io.nats.client.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import net.rankedproject.common.packet.sender.data.PacketSendingData;
import net.rankedproject.common.rest.RestCrudAPI;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PacketSenderImpl implements PacketSender {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final Injector injector;

    @Override
    public <T extends GeneratedMessage> CompletableFuture<?> send(
            @NotNull PacketSendingData<T, ?> packetSendingData
    ) {
        var packet = packetSendingData.sendingPacket();
        var byteArray = packet == null ? EMPTY_BYTE_ARRAY : packet.toByteArray();

        var subject = packetSendingData.subject();
        var connection = injector.getInstance(NatsInstantiator.class).get();

        return CompletableFuture.runAsync(() -> connection.publish(subject, byteArray), RestCrudAPI.EXECUTOR_SERVICE);
    }

    @Override
    public <T extends GeneratedMessage, U extends GeneratedMessage> CompletableFuture<U> sendAwaiting(
            @NotNull PacketSendingData<T, U> packetSendingData
    ) {
        var packet = packetSendingData.sendingPacket();
        var byteArray = packet == null ? EMPTY_BYTE_ARRAY : packet.toByteArray();

        var timeout = packetSendingData.timeout();
        var subject = packetSendingData.subject();

        var connection = injector.getInstance(NatsInstantiator.class).get();
        return connection.requestWithTimeout(subject, byteArray, timeout)
                .thenApply(message -> parsePacketDataFromMessage(packetSendingData, message));
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T extends GeneratedMessage, U extends GeneratedMessage> U parsePacketDataFromMessage(
            @NotNull PacketSendingData<T, U> packetSendingData,
            @NotNull Message message
    ) {
        var awaitingPacketClass = packetSendingData.awaitingPacket();
        if (awaitingPacketClass == null) {
            return null;
        }

        var parseMethod = awaitingPacketClass.getMethod("parseFrom", byte[].class);
        return (U) parseMethod.invoke(null, (Object) message.getData());
    }
}