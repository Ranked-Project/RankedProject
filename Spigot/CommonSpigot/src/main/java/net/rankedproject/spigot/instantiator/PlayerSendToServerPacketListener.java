package net.rankedproject.spigot.instantiator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SendPlayerToServer;
import net.rankedproject.spigot.CommonPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerSendToServerPacketListener implements PacketListener<SendPlayerToServer> {

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public Class<SendPlayerToServer> getPacketType() {
        return SendPlayerToServer.class;
    }

    @NotNull
    @Override
    public String getSubject() {
        var identifier = plugin.getRankedServer().name();
        return "server.player.%s".formatted(identifier);
    }

    @Override
    public void onPacket(@NotNull SendPlayerToServer packet) {
        var playerUUID = UUID.fromString(packet.getPlayerUuid());

    }
}
