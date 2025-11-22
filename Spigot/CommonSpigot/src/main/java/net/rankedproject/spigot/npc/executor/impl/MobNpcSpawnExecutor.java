package net.rankedproject.spigot.npc.executor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Singleton
@RequiredArgsConstructor
public class MobNpcSpawnExecutor implements NpcSpawnExecutor {

    @Override
    public void spawnEntity(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        var npc = loadedNpc.npc();

        var nmsEntityType = loadedNpc.npc().getBehavior().entityType();
        var bukkitEntityType = CraftEntityType.minecraftToBukkit(nmsEntityType);

        var player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        var packetPlayerManager = PacketEvents.getAPI().getPlayerManager();

        int entitySize = loadedNpc.npc().getBehavior().entitySize();
        var entitySizeProperty = new WrapperPlayServerUpdateAttributes.Property(Attributes.SCALE, entitySize, List.of());
        var updateAttributesPacket = new WrapperPlayServerUpdateAttributes(loadedNpc.entityId(), List.of(entitySizeProperty));

        var location = npc.getBehavior().location();
        var entitySpawnPacket = new WrapperPlayServerSpawnEntity(
                loadedNpc.entityId(),
                UUID.randomUUID(),
                SpigotConversionUtil.fromBukkitEntityType(bukkitEntityType),
                SpigotConversionUtil.fromBukkitLocation(location),
                location.getYaw(),
                0,
                null
        );

        packetPlayerManager.sendPacket(player, entitySpawnPacket);
        packetPlayerManager.sendPacket(player, updateAttributesPacket);
    }
}