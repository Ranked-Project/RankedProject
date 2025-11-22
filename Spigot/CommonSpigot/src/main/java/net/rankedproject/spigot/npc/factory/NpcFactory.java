package net.rankedproject.spigot.npc.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory responsible for creating NPCs (Non-Player Characters)
 * for individual players.
 * <p>
 * This class integrates with {@link NpcSpawnedTracker} to keep track of spawned NPCs per player
 * and uses {@link Injector} to instantiate NPC instances with dependency injection.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * NpcFactory factory = ...;
 * UUID playerUUID = player.getUniqueId();
 * int entityId = factory.create(MyCustomNpc.class, playerId);
 * factory.remove(entityId, playerId);
 * }</pre>
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcFactory {

    private final NpcSpawnedTracker npcSpawnedTracker;
    private final Injector injector;

    /**
     * Creates a new NPC of the specified type for the given player, using a provided entity ID.
     * <p>
     * The NPC is tracked in {@link NpcSpawnedTracker} and spawned using its configured spawn executor.
     * </p>
     *
     * @param npcType    The class type of the NPC to instantiate. Must extend {@link Npc}.
     * @param playerUUID The UUID of the player for whom the NPC is created.
     * @param entityId   The entity ID to assign to the spawned NPC.
     * @return The entity ID of the newly created NPC.
     */
    public <T extends Npc> int create(@NotNull UUID playerUUID, @NotNull Class<T> npcType, int entityId) {
        if (npcSpawnedTracker.getNpcById(playerUUID, entityId) != null) {
            return -1;
        }

        var npc = injector.getInstance(npcType);
        var loadedNpc = LoadedNpc.builder()
                .npc(npc)
                .entityId(entityId)
                .build();

        npcSpawnedTracker.track(loadedNpc, playerUUID);
        return entityId;
    }

    /**
     * Creates a new NPC of the specified type for the given player with an automatically
     * generated entity ID.
     *
     * @param npcType    The class type of the NPC to instantiate. Must extend {@link Npc}.
     * @param playerUUID The UUID of the player for whom the NPC is created.
     * @return The entity ID of the newly created NPC.
     */
    public <T extends Npc> int create(@NotNull UUID playerUUID, @NotNull Class<T> npcType) {
        int entityId = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        return create(playerUUID, npcType, entityId);
    }

    /**
     * Removes the NPC with the specified entity ID for the given player.
     * <p>
     * This method untracks the NPC from {@link NpcSpawnedTracker} and effectively removes it
     * from the player's world.
     * </p>
     *
     * @param loadedNpc   The LoadedNpc of the NPC to remove.
     * @param playerUUID The UUID of the player associated with the NPC.
     */
    public void remove(@NotNull UUID playerUUID, @NotNull LoadedNpc loadedNpc) {
        var npcSpawnExecutor = loadedNpc.npc().getNpcSpawnExecutor();
        npcSpawnExecutor.despawnEntity(loadedNpc, playerUUID);
    }
}