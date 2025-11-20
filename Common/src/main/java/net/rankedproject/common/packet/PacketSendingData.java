package net.rankedproject.common.packet;

import com.google.inject.Injector;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import lombok.Builder;

import java.util.function.Consumer;

@Builder
public record PacketSendingData<T extends GeneratedMessage, U extends GeneratedMessage>(
        Injector injector,
        String subject,
        T packet,
        Consumer<? super U> response) {

    public void send() {
        injector.getInstance(PacketSender.class);
    }
}
