package net.rankedproject.velocity.server.listener.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.PlayerSendToServer;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerServerSendListener implements PacketListener<PlayerSendToServer> {

    private final ProxyServer proxyServer;
    private final ServerTracker serverTracker;

    @Override
    public @NotNull Class<PlayerSendToServer> getPacketType() {
        return PlayerSendToServer.class;
    }

    @Override
    public @NotNull String getSubject() {
        return "player.server.send.specific";
    }

    @Override
    public void onPacket(@NotNull PlayerSendToServer packet) {
        var playerUUID = UUID.fromString(packet.getPlayerUuid());
        var player = proxyServer.getPlayer(playerUUID).orElse(null);
        if (player == null) {
            return;
        }

        var identifier = packet.getServerIdentifier();
        var loadedServer = serverTracker.get(identifier);

        if (loadedServer == null) {
            return;
        }

        var registeredServer = proxyServer.getServer(identifier).orElse(null);
        if (registeredServer == null) {
            return;
        }

        player.createConnectionRequest(registeredServer).connect();
    }
}
