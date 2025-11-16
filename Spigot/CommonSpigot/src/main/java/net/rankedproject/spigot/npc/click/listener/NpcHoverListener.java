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
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import net.rankedproject.spigot.util.raytrace.Area;
import net.rankedproject.spigot.util.raytrace.RayTraceUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
        var isPacketAccess = PACKET_TYPES
                .stream()
                .anyMatch(packetType -> packetType.equals(event.getPacketType()));
        if (!isPacketAccess) return;

        Player player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var location = player.getLocation();
        var eyeVector3d = new Vector3d(location.getX(), location.getY() + player.getEyeHeight(), location.getZ());

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        npcSpawnedTracker.getNpcList(playerUUID)
                .forEach(loadedNpc -> {
                    var npc = loadedNpc.npc();
                    var npcClickBehavior = npc.getBehavior().clickBehavior();
                    if (npcClickBehavior == null) return;

                    double entityInteractRange = Objects.requireNonNull(player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)).getBaseValue();
                    Vector3d look = RayTraceUtil.getLookVector(yaw, pitch).multiply(entityInteractRange);
                    Vector3d target = eyeVector3d.add(look);

                    var area = getArea(npc);
                    Vector3d intercept = RayTraceUtil.calculateIntercept(area, eyeVector3d, target);
                    boolean entityExists = intercept != null;

                    setEntityGlow(playerUUID, loadedNpc.entityId(), entityExists);
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

    @NotNull
    private Area getArea(
            @NotNull Npc npc
    ) {
        var location = npc.getBehavior().location();
        var entitySize = npc.getBehavior().entitySize();

        double entityHitboxWidth = (npc.getBehavior().entityType().getWidth() - 0.25) * entitySize;
        double entityHitboxHeight = npc.getBehavior().entityType().getHeight() * entitySize;

        return new Area(
                location.x() - entityHitboxWidth, location.y(), location.z() - entityHitboxWidth,
                location.x() + entityHitboxWidth, location.y() + entityHitboxHeight, location.z() + entityHitboxWidth
        );
    }
}