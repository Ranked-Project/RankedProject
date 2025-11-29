package net.rankedproject.velocity.server.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.common.server.Server;
import net.rankedproject.velocity.VelocityProxy;
import net.rankedproject.velocity.server.ServerTracker;
import net.rankedproject.common.util.ServerType;
import net.rankedproject.proto.SpigotServerConnect;
import net.rankedproject.velocity.server.data.LoadedServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SpigotServerConnectPacketListener implements PacketListener<SpigotServerConnect> {

    private final VelocityProxy plugin;
    private final ServerTracker serverTracker;

    @NotNull
    @Override
    public Class<SpigotServerConnect> getPacketType() {
        return SpigotServerConnect.class;
    }

    @NotNull
    @Override
    public String getSubject() {
        return "spigot.server.connect";
    }

    @Override
    public void onPacket(@NotNull SpigotServerConnect packet) {
        log.info("Server connected to server: {}", packet);
        var serverUUID = UUID.fromString(packet.getServerUuid());
        var serverType = ServerType.valueOf(packet.getServerType());
        var inetSocketAddress = new InetSocketAddress(packet.getHostName(), packet.getPort());

        var server = new Server(serverUUID, serverType, inetSocketAddress, Set.of());
        var loadedServer = new LoadedServer(System.currentTimeMillis(), server);

        log.info("Server connected to server: {}", server);

        serverTracker.track(loadedServer);
        log.info("Server {} has been tracked", serverUUID);

        plugin.getProxyServer().registerServer(new ServerInfo(packet.getServerUuid(), inetSocketAddress));
    }
}