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
public final class SpigotServerDisconnectPacketListener implements PacketListener<SpigotServerDisconnect> {

    private final ServerInstanceFacade serverInstanceFacade;

    @Override
    public @NotNull Class<SpigotServerDisconnect> getPacketType() {
        return SpigotServerDisconnect.class;
    }

    @Override
    public @NotNull String getSubject() {
        return "server.spigot.disconnect";
    }

    @Override
    public void onPacket(final SpigotServerDisconnect packet) {
        this.serverInstanceFacade.removeServer(packet.getServerIdentifier());
    }
}
