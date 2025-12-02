package net.rankedproject.velocity.server.listener.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SpigotServerDisconnect;
import net.rankedproject.velocity.server.ServerInstanceFacade;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SpigotServerDisconnectPacketListener implements PacketListener<SpigotServerDisconnect> {

    private final ServerInstanceFacade serverInstanceFacade;

    @NotNull
    @Override
    public Class<SpigotServerDisconnect> getPacketType() {
        return SpigotServerDisconnect.class;
    }

    @NotNull
    @Override
    public String getSubject() {
        return "server.spigot.disconnect";
    }

    @Override
    public void onPacket(@NotNull SpigotServerDisconnect packet) {
        serverInstanceFacade.removeServer(packet.getServerIdentifier());
    }
}