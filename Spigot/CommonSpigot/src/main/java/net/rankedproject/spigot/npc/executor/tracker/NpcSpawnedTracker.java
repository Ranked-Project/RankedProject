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

    private final Map<UUID, Map<Integer, LoadedNpc<?>>> spawnedNpcMap = new ConcurrentHashMap<>();

    public <T extends Npc> void track(@NotNull LoadedNpc<T> loadedNpc, UUID playerUUID) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new HashMap<>()).put(loadedNpc.entityId(), loadedNpc);
    }

    public <T extends Npc> void untrack(@NotNull LoadedNpc<T> loadedNpc, UUID playerUUID) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new HashMap<>()).remove(loadedNpc.entityId());
    }

    @SuppressWarnings("unchecked")
    public <T extends Npc> LoadedNpc<T> getNpcById(@NotNull UUID playerUUID, int entityId) {
        return (LoadedNpc<T>) spawnedNpcMap.getOrDefault(playerUUID, Collections.emptyMap()).get(entityId);
    }

    @UnmodifiableView
    public Map<Integer, LoadedNpc<?>> getNpcMap(@NotNull UUID playerUUID) {
        return spawnedNpcMap.getOrDefault(playerUUID, Collections.emptyMap());
    }
}