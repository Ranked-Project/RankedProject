package net.rankedproject.common.packet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PacketSenderImpl implements PacketSender {

    private final Injector injector;

    @Override
    public void send(@NotNull PacketSendingData<?, ?> sendingData) {
        var packet = sendingData.packet();
        var byteArray = packet.toByteArray();

        var connection = injector.getInstance(NatsInstantiator.class).get();
        connection.publish(sendingData.subject(), byteArray);
    }
}
