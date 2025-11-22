package net.rankedproject.common.packet;

import com.google.common.reflect.Reflection;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.protobuf.GeneratedMessage;
import lombok.RequiredArgsConstructor;
import net.rankedproject.CorePacket;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PacketSenderImpl implements PacketSender {

    private final Injector injector;

    @Override
    public <T extends GeneratedMessage, U extends GeneratedMessage> CompletableFuture<U> send(
            @NotNull PacketSendingData<T, U> packetSendingData
    ) {
        var packet = packetSendingData.sendingPacket();
        var byteArray = packet.toByteArray();

        var timeout = packetSendingData.timeout();
        var subject = packetSendingData.subject();

        var connection = injector.getInstance(NatsInstantiator.class).get();
        return connection.requestWithTimeout(subject, byteArray, timeout)
                .thenApply(message -> {
                    Class<U> awaitingPacketClass = packetSendingData.awaitingPacket();
                    try {
                        Method parseMethod = awaitingPacketClass.getMethod("parseFrom", byte[].class);
                        parseMethod.invoke()
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                    CorePacket.SendPlayerToServer.parseFrom()
                    packetSendingData.awaitingPacket()

                    return message.getData()
                });
    }
}