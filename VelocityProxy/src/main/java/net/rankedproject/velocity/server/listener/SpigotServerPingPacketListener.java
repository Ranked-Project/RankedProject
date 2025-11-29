package net.rankedproject.velocity.server.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.SpigotServerPing;
import net.rankedproject.velocity.server.ServerTracker;
import net.rankedproject.velocity.server.data.LoadedServer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SpigotServerPingPacketListener implements PacketListener<SpigotServerPing> {

    private final ServerTracker serverTracker;

    @NotNull
    @Override
    public Class<SpigotServerPing> getPacketType() {
        return SpigotServerPing.class;
    }

    @NotNull
    @Override
    public String getSubject() {
        return "spigot.server.ping";
    }

    @Override
    public void onPacket(@NotNull SpigotServerPing packet) {
        var serverUUID = UUID.fromString(packet.getServerUuid());
        var loadedServer = serverTracker.get(serverUUID);

        if (loadedServer == null) {
            return;
        }

        var updatedLoadedServer = new LoadedServer(System.currentTimeMillis(), loadedServer.server());
        serverTracker.track(updatedLoadedServer);
        log.info("[VelocityProxy] Server Ping: [{}]", updatedLoadedServer);
    }
}
