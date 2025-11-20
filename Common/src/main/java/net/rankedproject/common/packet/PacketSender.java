package net.rankedproject.common.packet;

import com.google.inject.Injector;
import com.google.protobuf.GeneratedMessage;
import org.jetbrains.annotations.NotNull;

public interface PacketSender {

    default <T extends GeneratedMessage, U extends GeneratedMessage> PacketSendingData.PacketSendingDataBuilder<T, U> builder(
            @NotNull Injector injector
    ) {
        return PacketSendingData.<T, U>builder()
                .injector(injector);
    }

    void send(@NotNull PacketSendingData<?, ?> sendingData);
}
