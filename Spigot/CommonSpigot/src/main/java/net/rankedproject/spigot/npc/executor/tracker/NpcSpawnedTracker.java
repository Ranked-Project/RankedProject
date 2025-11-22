package net.rankedproject.spigot.npc.executor.tracker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcSpawnedTracker {

    private final Map<UUID, Int2ObjectMap<LoadedNpc>> spawnedNpcMap = new ConcurrentHashMap<>();

    public void track(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new Int2ObjectOpenHashMap<>()).put(loadedNpc.entityId(), loadedNpc);
    }

    public void untrack(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new Int2ObjectOpenHashMap<>()).remove(loadedNpc.entityId());
    }

    public LoadedNpc getNpcById(@NotNull UUID playerUUID, int entityId) {
        return spawnedNpcMap.getOrDefault(playerUUID, Int2ObjectMaps.emptyMap()).get(entityId);
    }

    @UnmodifiableView
    public Int2ObjectMap<LoadedNpc> getNpcMap(@NotNull UUID playerUUID) {
        return spawnedNpcMap.getOrDefault(playerUUID, Int2ObjectMaps.emptyMap());
    }
}