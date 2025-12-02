package net.rankedproject.common.network.player.data;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NetworkPlayer {

    private final UUID playerUUID;

    private UUID serverUUID;
}
