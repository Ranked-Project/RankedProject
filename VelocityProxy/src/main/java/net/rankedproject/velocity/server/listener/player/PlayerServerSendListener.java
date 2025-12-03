package net.rankedproject.velocity.server.listener.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.PlayerSendToServer;
import net.rankedproject.velocity.server.tracker.ServerTracker;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class PlayerServerSendListener implements PacketListener<PlayerSendToServer> {

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
    public void onPacket(final PlayerSendToServer packet) {
        var playerUUID = UUID.fromString(packet.getPlayerUuid());
        var player = this.proxyServer.getPlayer(playerUUID).orElse(null);
        if (player == null) {
            return;
        }

        var identifier = packet.getServerIdentifier();
        var loadedServer = this.serverTracker.get(identifier);

        if (loadedServer == null) {
            return;
        }

        var registeredServer = this.proxyServer.getServer(identifier).orElse(null);
        if (registeredServer == null) {
            return;
        }

        player.createConnectionRequest(registeredServer).connect();
    }
}
