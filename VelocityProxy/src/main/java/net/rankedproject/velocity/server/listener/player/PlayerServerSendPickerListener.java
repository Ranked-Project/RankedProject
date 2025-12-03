package net.rankedproject.velocity.server.listener.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.common.packet.listener.PacketListener;
import net.rankedproject.proto.PlayerSendToServerByPicker;
import net.rankedproject.velocity.server.picker.registry.ServerPickerRegistry;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class PlayerServerSendPickerListener implements PacketListener<PlayerSendToServerByPicker> {

    private final ProxyServer proxyServer;
    private final ServerPickerRegistry serverPickerRegistry;

    @Override
    public @NotNull Class<PlayerSendToServerByPicker> getPacketType() {
        return PlayerSendToServerByPicker.class;
    }

    @Override
    public @NotNull String getSubject() {
        return "player.server.send.picker";
    }

    @Override
    public void onPacket(final PlayerSendToServerByPicker packet) {
        var playerUUID = UUID.fromString(packet.getPlayerUuid());
        var serverType = ServerType.valueOf(packet.getServerType());
        var serverPickerType = ServerPickerType.valueOf(packet.getServerPickerType());

        var player = this.proxyServer.getPlayer(playerUUID).orElse(null);
        if (player == null) {
            return;
        }

        var pickedServer = this.serverPickerRegistry
                .get(serverPickerType)
                .getServer(playerUUID, serverType);

        this.proxyServer
                .getServer(pickedServer.getServerInfo().getName())
                .ifPresent(registeredServer -> player.createConnectionRequest(registeredServer).connect());
    }
}
