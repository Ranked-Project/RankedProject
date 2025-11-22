package net.rankedproject.spigot.npc.executor;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.google.inject.Singleton;
import net.rankedproject.spigot.npc.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Handles spawning and despawning of NPCs for specific players.
 * <p>
 * Implementations of this interface are responsible for visually displaying NPC entities
 * to the player in-game and managing their lifecycle on a per-player basis.
 * </p>
 */
public interface NpcSpawnExecutor {

    /**
     * Spawns the given NPC for a specific player.
     * <p>
     * The NPC will be visually shown to the player, based on the NPC's model and behavior.
     * </p>
     *
     * @param npc the NPC to spawn
     * @param playerUUID the UUID of the player who should see the NPC
     */
    void spawnEntity(@NotNull LoadedNpc npc, @NotNull UUID playerUUID);

    /**
     * Despawns the given NPC for a specific player.
     * <p>
     * By default, this method sends a destroy entity packet to the player
     * to remove the NPC visually.
     * </p>
     *
     * @param loadedNpc the NPC to despawn
     * @param playerUUID the UUID of the player who should no longer see the NPC
     */
    default void despawnEntity(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));

        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(loadedNpc.entityId());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
    }
}