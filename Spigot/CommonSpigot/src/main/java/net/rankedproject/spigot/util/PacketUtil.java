package net.rankedproject.spigot.util;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PacketUtil {

    public int getEntityIdFromEvent(@NotNull PacketReceiveEvent event) {
        return readVarInt(event.getByteBuf());
    }

    public int readVarInt(@NotNull Object buffer) {
        int value = 0;
        int length = 0;
        byte currentByte;
        do {
            currentByte = ByteBufHelper.readByte(buffer);
            value |= (currentByte & 0x7F) << (length * 7);
            length++;
            if (length > 5) {
                throw new RuntimeException("VarInt is too large. Must be smaller than 5 bytes.");
            }
        } while ((currentByte & 0x80) == 0x80);
        return value;
    }
}
