package net.rankedproject.spigot.npc.click.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import net.rankedproject.spigot.util.raytrace.Area;
import net.rankedproject.spigot.util.raytrace.EntityRayTraceUtil;
import net.rankedproject.spigot.util.raytrace.RayTraceUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    private final NpcSpawnedTracker npcSpawnedTracker;

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        var isPacketAccess = PACKET_TYPES.stream().anyMatch(packetType -> packetType.equals(event.getPacketType()));
        if (!isPacketAccess) {
            return;
        }

        Player player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        npcSpawnedTracker.getNpcMap(playerUUID)
                .values()
                .forEach(loadedNpc -> {
                    var npc = loadedNpc.npc();
                    var npcClickBehavior = npc.getBehavior().clickBehavior();
                    if (npcClickBehavior == null) return;

                    var location = npc.getBehavior().location();
                    var entityType = npc.getBehavior().entityType();
                    var entitySize = npc.getBehavior().entitySize();

                    boolean isLookingAtEntity = EntityRayTraceUtil.isLookingAtEntity(player, location, entityType, entitySize);

                    setEntityGlow(playerUUID, loadedNpc.entityId(), isLookingAtEntity);
                });
    }

    private void setEntityGlow(
            @NotNull UUID playerUUID,
            int entityId,
            boolean glowing
    ) {
        var player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        List<EntityData<?>> entityGlowingFlags = glowing
                ? ENTITY_GLOWING_TRUE_FLAGS
                : ENTITY_GLOWING_FALSE_FLAGS;

        var packet = new WrapperPlayServerEntityMetadata(entityId, entityGlowingFlags);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}