package net.rankedproject.common.network.player.data;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class NetworkPlayer {

    private final UUID playerUUID;

    private UUID serverUUID;
}