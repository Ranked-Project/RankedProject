package net.rankedproject.spigot.npc.executor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.type.MobNpc;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.entity.Player;

import java.util.*;

@Singleton
@RequiredArgsConstructor
public class MobNpcSpawnExecutor implements NpcSpawnExecutor {

    @Override
    public void spawnEntity(LoadedNpc loadedNpc, UUID playerUUID) {
        var npc = loadedNpc.npc();
        var mobNpc = (MobNpc) npc;

        var location = mobNpc.getBehavior().location();

        var nmsEntityType = loadedNpc.npc().getBehavior().entityType();
        var bukkitEntityType = CraftEntityType.minecraftToBukkit(nmsEntityType);

        var entitySpawnPacket = new WrapperPlayServerSpawnEntity(
                loadedNpc.entityId(),
                UUID.randomUUID(),
                SpigotConversionUtil.fromBukkitEntityType(bukkitEntityType),
                SpigotConversionUtil.fromBukkitLocation(location),
                location.getYaw(),
                0,
                null
        );

        var player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        var packetPlayerManager = PacketEvents.getAPI().getPlayerManager();

        var entitySizeProperty = new WrapperPlayServerUpdateAttributes.Property(
                Attributes.SCALE,
                loadedNpc.npc().getBehavior().entitySize(),
                List.of()
        );
        var updateAttributesPacket = new WrapperPlayServerUpdateAttributes(loadedNpc.entityId(), List.of(entitySizeProperty));

        packetPlayerManager.sendPacket(player, entitySpawnPacket);
        packetPlayerManager.sendPacket(player, updateAttributesPacket);
    }

    @Override
    public void despawnEntity(LoadedNpc loadedNpc, UUID playerUUID) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));

        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(loadedNpc.entityId());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
    }
}
