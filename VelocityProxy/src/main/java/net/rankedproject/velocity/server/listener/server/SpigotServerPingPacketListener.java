package net.rankedproject.velocity.server.listener.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import java.net.InetSocketAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SpigotServerPing;
import net.rankedproject.velocity.server.ServerInstanceFacade;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class SpigotServerPingPacketListener implements PacketListener<SpigotServerPing> {

    private final ServerInstanceFacade serverInstanceFacade;
    private final ProxyServer proxyServer;
    private final ServerTracker serverTracker;

    @Override
    public @NotNull Class<SpigotServerPing> getPacketType() {
        return SpigotServerPing.class;
    }

    @Override
    public @NotNull String getSubject() {
        return "server.spigot.ping";
    }

    @Override
    public void onPacket(final SpigotServerPing packet) {
        var identifier = packet.getServerIdentifier();
        var loadedServer = this.serverTracker.get(identifier);

        if (loadedServer == null) {
            var serverType = ServerType.valueOf(packet.getServerType());
            var inetSocketAddress = new InetSocketAddress(packet.getHostName(), packet.getPort());

            this.serverInstanceFacade.createServer(identifier, serverType, inetSocketAddress);
            return;
        }

        var registeredServer = this.proxyServer.getServer(identifier).orElse(null);
        if (registeredServer == null) {
            this.serverInstanceFacade.loadServer(loadedServer);
            return;
        }

        this.serverInstanceFacade.updateServer(
                identifier, loadedServerUpdate ->
                        loadedServerUpdate.setLastPingMs(System.currentTimeMillis())
        );
    }
}
