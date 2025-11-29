package net.rankedproject.spigot.npc.click.listener;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import net.rankedproject.spigot.util.raytrace.EntityRayTraceUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcHoverListener implements PacketListener {

    private static final List<PacketType.Play.Client> PACKET_TYPES = List.of(
            PacketType.Play.Client.PLAYER_POSITION,
            PacketType.Play.Client.PLAYER_ROTATION,
            PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION
    );

    private static final List<EntityData<?>> ENTITY_GLOWING_TRUE_FLAGS = new ArrayList<>(
            List.of(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x40))
    );

    private static final List<EntityData<?>> ENTITY_GLOWING_FALSE_FLAGS = new ArrayList<>(
            List.of(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0))
    );

    private final LoadingCache<@NotNull UUID, Int2BooleanOpenHashMap> lastPacket = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(1))
            .build(_ -> new Int2BooleanOpenHashMap());

    private final NpcSpawnedTracker npcSpawnedTracker;

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        var isExpectedPacketType = PACKET_TYPES.stream().anyMatch(packetType -> packetType.equals(event.getPacketType()));
        if (!isExpectedPacketType) {
            return;
        }

        var player = event.<Player>getPlayer();
        var playerUUID = player.getUniqueId();

        var loadedNpcs = npcSpawnedTracker.getNpcMap(playerUUID).values();
        for (LoadedNpc loadedNpc : loadedNpcs) {
            var npc = loadedNpc.npc();
            var behavior = npc.getBehavior();

            var hasHoverEffect = behavior.clickBehaviorMetadata() != null;
            if (!hasHoverEffect) {
                continue;
            }

            var location = behavior.location();
            var entityType = behavior.entityType();
            var entitySize = behavior.entitySize();

            boolean isLookingAtEntity = EntityRayTraceUtil.isLookingAtEntity(player, location, entityType, entitySize);

            var lastGlowingStatePerEntityMap = Objects.requireNonNull(lastPacket.get(playerUUID));
            var lastWasGlowing = lastGlowingStatePerEntityMap.get(loadedNpc.entityId());

            if (isLookingAtEntity && !lastWasGlowing) {
                setEntityGlow(playerUUID, loadedNpc.entityId(), true);
            } else if (!isLookingAtEntity && lastWasGlowing) {
                setEntityGlow(playerUUID, loadedNpc.entityId(), false);
            }
        }
    }

    private void setEntityGlow(@NotNull UUID playerUUID, int entityId, boolean glowing) {
        var player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        var entityGlowingFlags = glowing ? ENTITY_GLOWING_TRUE_FLAGS : ENTITY_GLOWING_FALSE_FLAGS;

        var packet = new WrapperPlayServerEntityMetadata(entityId, entityGlowingFlags);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

        var lastGlowingStatePerEntityMap = Objects.requireNonNull(lastPacket.get(playerUUID));
        lastGlowingStatePerEntityMap.put(entityId, glowing);
    }
}