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

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcFactory {

    private final NpcSpawnedTracker npcSpawnedTracker;
    private final Injector injector;

    public int create(
            @NotNull Class<? extends Npc> npcType,
            @NotNull UUID playerUUID,
            int entityId
    ) {
        var npc = injector.getInstance(npcType);
        var loadedNpc = LoadedNpc.builder()
                .npc(npc)
                .entityId(entityId)
                .build();

        npcSpawnedTracker.track(playerUUID, loadedNpc);
        return entityId;
    }

    public int create(
            @NotNull Class<? extends Npc> npcType,
            @NotNull UUID playerUUID
    ) {
        int entityId = UUID.randomUUID().hashCode();
        return this.create(npcType, playerUUID, entityId);
    }

    public void remove(
            int entityId,
            @NotNull UUID playerUUID
    ) {
        var npcOptional = npcSpawnedTracker.getNpcList(playerUUID)
                .stream()
                .filter(it -> it.entityId() == entityId)
                .findFirst();
        if (npcOptional.isEmpty()) return;

        var loadedNpc = npcOptional.get();
        npcSpawnedTracker.untrack(playerUUID, loadedNpc);
    }
}