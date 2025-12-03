package net.rankedproject.velocity.server.listener.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.InetSocketAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SpigotServerConnect;
import net.rankedproject.velocity.server.ServerInstanceFacade;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class SpigotServerConnectPacketListener implements PacketListener<SpigotServerConnect> {

    private final ServerInstanceFacade serverInstanceFacade;

    @Override
    public @NotNull Class<SpigotServerConnect> getPacketType() {
        return SpigotServerConnect.class;
    }

    @Override
    public @NotNull String getSubject() {
        return "server.spigot.connect";
    }

    @Override
    public void onPacket(final @NotNull SpigotServerConnect packet) {
        var identifier = packet.getServerIdentifier();
        var serverType = ServerType.valueOf(packet.getServerType());
        var inetSocketAddress = new InetSocketAddress(packet.getHostName(), packet.getPort());

        this.serverInstanceFacade.createServer(identifier, serverType, inetSocketAddress);
    }
}
