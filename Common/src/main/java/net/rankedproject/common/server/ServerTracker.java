package net.rankedproject.common.server;

import net.rankedproject.CorePacket;
import net.rankedproject.common.packet.PacketSender;

public class ServerTracker {

    private final PacketSender packetSender;

    public ServerTracker(PacketSender packetSender) {
        this.packetSender = packetSender;
    }

    public void test() {
        var packet = CorePacket.SendPlayerToServer.newBuilder()
                .setServerName("lobby-server-3")
                .setPlayerUuid("randomUUID")
                .build();

        packetSender.builder(null)
                .subject("lobby-server-*")
                .packet(packet)
                .build()
                .send();
    }

}
