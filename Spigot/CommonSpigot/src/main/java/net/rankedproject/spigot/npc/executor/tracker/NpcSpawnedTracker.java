package net.rankedproject.spigot.npc.executor.tracker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcSpawnedTracker {

    private final Map<UUID, Map<Integer, LoadedNpc>> spawnedNpcMap = new ConcurrentHashMap<>();

    public void track(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new HashMap<>()).put(loadedNpc.entityId(), loadedNpc);
    }

    public void untrack(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new HashMap<>()).remove(loadedNpc.entityId());
    }

    public LoadedNpc getNpcById(@NotNull UUID playerUUID, int entityId) {
        return spawnedNpcMap.getOrDefault(playerUUID, Collections.emptyMap()).get(entityId);
    }

    @UnmodifiableView
    public Map<Integer, LoadedNpc> getNpcMap(@NotNull UUID playerUUID) {
        return spawnedNpcMap.getOrDefault(playerUUID, Collections.emptyMap());
    }
}