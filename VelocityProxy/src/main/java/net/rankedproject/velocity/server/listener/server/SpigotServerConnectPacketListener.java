package net.rankedproject.velocity.server.listener.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SpigotServerConnect;
import net.rankedproject.velocity.server.ServerInstanceFacade;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SpigotServerConnectPacketListener implements PacketListener<SpigotServerConnect> {

    private final ServerInstanceFacade serverInstanceFacade;

    @NotNull
    @Override
    public Class<SpigotServerConnect> getPacketType() {
        return SpigotServerConnect.class;
    }

    @NotNull
    @Override
    public String getSubject() {
        return "server.spigot.connect";
    }

    @Override
    public void onPacket(@NotNull SpigotServerConnect packet) {
        var identifier = packet.getServerIdentifier();
        var serverType = ServerType.valueOf(packet.getServerType());
        var inetSocketAddress = new InetSocketAddress(packet.getHostName(), packet.getPort());

        serverInstanceFacade.createServer(identifier, serverType, inetSocketAddress);
    }
}