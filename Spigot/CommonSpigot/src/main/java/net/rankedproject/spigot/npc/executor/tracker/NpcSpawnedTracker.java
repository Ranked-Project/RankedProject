package net.rankedproject.spigot.npc.executor.tracker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcSpawnedTracker {

    private final Map<UUID, List<LoadedNpc>> spawnedNpcMap = new ConcurrentHashMap<>();

    public void track(@NotNull UUID playerUUID, LoadedNpc loadedNpc) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new ArrayList<>()).add(loadedNpc);
    }

    public void untrack(@NotNull UUID playerUUID, LoadedNpc loadedNpc) {
        spawnedNpcMap.computeIfAbsent(playerUUID, _ -> new ArrayList<>()).remove(loadedNpc);
    }

    @UnmodifiableView
    public List<LoadedNpc> getNpcList(@NotNull UUID playerUUID) {
        return spawnedNpcMap.getOrDefault(playerUUID, Collections.emptyList());
    }
}