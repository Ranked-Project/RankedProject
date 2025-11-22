package net.rankedproject.common.packet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.protobuf.GeneratedMessage;
import lombok.RequiredArgsConstructor;
import net.rankedproject.CorePacket;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import org.jetbrains.annotations.NotNull;

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

        var connection = injector.getInstance(NatsInstantiator.class).get();

        return CompletableFuture.supplyAsync(() -> connection.publish(packetSendingData.subject(), byteArray));
    }
}