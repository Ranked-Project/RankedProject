package net.rankedproject.spigot.npc.executor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.model.impl.EntityNpcModel;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.tracker.ModelScaler;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class MobNpcSpawnExecutor implements NpcSpawnExecutor {

    @Override
    public void spawnEntity(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        var npc = loadedNpc.npc();
        var model = npc.getBehavior().model();
        var renderer = model instanceof EntityNpcModel entityModel
                ? BetterModel.model(entityModel.getModelId()).orElseThrow(() ->
                    new IllegalStateException("BetterModel model is not loaded: " + entityModel.getModelId()))
                : null;

        if (loadedNpc.modelTracker() != null) {
            despawnEntity(loadedNpc, playerUUID);
        }

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
                renderer == null ? SpigotConversionUtil.fromBukkitEntityType(bukkitEntityType) : EntityTypes.INTERACTION,
                SpigotConversionUtil.fromBukkitLocation(location),
                location.getYaw(),
                0,
                null
        );

        packetPlayerManager.sendPacket(player, entitySpawnPacket);
        if (renderer == null) {
            packetPlayerManager.sendPacket(player, updateAttributesPacket);
        }
        if (renderer != null) {
            // An invisible interaction entity preserves the framework's ID and click handling.
            var dimensions = nmsEntityType.getDimensions();
            packetPlayerManager.sendPacket(player, new WrapperPlayServerEntityMetadata(
                    loadedNpc.entityId(), List.of(
                            new EntityData<>(8, EntityDataTypes.FLOAT, dimensions.width() * entitySize),
                            new EntityData<>(9, EntityDataTypes.FLOAT, dimensions.height() * entitySize),
                            new EntityData<>(10, EntityDataTypes.BOOLEAN, true))));
            var tracker = renderer.create(location.clone());
            loadedNpc.modelTracker(tracker);
            try {
                tracker.scaler(ModelScaler.value(entitySize));
                tracker.spawn(player);
            } catch (RuntimeException exception) {
                despawnEntity(loadedNpc, playerUUID);
                throw exception;
            }
        }
    }
}
