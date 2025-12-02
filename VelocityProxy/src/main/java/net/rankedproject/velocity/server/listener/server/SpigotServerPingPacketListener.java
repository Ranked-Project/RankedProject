package net.rankedproject.velocity.server.listener.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SpigotServerPing;
import net.rankedproject.velocity.server.ServerInstanceFacade;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SpigotServerPingPacketListener implements PacketListener<SpigotServerPing> {

    private final ServerInstanceFacade serverInstanceFacade;
    private final ProxyServer proxyServer;
    private final ServerTracker serverTracker;

    @NotNull
    @Override
    public Class<SpigotServerPing> getPacketType() {
        return SpigotServerPing.class;
    }

    @NotNull
    @Override
    public String getSubject() {
        return "server.spigot.ping";
    }

    @Override
    public void onPacket(@NotNull SpigotServerPing packet) {
        var identifier = packet.getServerIdentifier();
        var loadedServer = serverTracker.get(identifier);

        if (loadedServer == null) {
            var serverType = ServerType.valueOf(packet.getServerType());
            var inetSocketAddress = new InetSocketAddress(packet.getHostName(), packet.getPort());

            serverInstanceFacade.createServer(identifier, serverType, inetSocketAddress);
            return;
        }

        var registeredServer = proxyServer.getServer(identifier).orElse(null);
        if (registeredServer == null) {
            serverInstanceFacade.loadServer(loadedServer);
            return;
        }

        serverInstanceFacade.updateServer(identifier, loadedServerUpdate ->
                loadedServerUpdate.setLastPingMs(System.currentTimeMillis())
        );
    }
}
