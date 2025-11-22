package net.rankedproject.spigot.npc.executor.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcLoadListener implements Listener {

    private final NpcSpawnedTracker npcSpawnedTracker;

    @EventHandler
    public void onPlayerChunkLoad(@NotNull PlayerChunkLoadEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var chunk = event.getChunk();
        npcSpawnedTracker.getNpcMap(playerUUID)
                .values()
                .stream()
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().location().getChunk().equals(chunk))
                .forEach(loadedNpc -> {
                    var npcSpawnExecutor = loadedNpc.npc().getNpcSpawnExecutor();
                    npcSpawnExecutor.spawnEntity(loadedNpc, playerUUID);
                });
    }

    @EventHandler
    public void onPlayerChunkUnload(@NotNull PlayerChunkUnloadEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var chunk = event.getChunk();
        npcSpawnedTracker.getNpcMap(playerUUID)
                .values()
                .stream()
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().location().getChunk().equals(chunk))
                .forEach(loadedNpc -> {
                    var npcSpawnExecutor = loadedNpc.npc().getNpcSpawnExecutor();
                    npcSpawnExecutor.despawnEntity(loadedNpc, playerUUID);
                });
    }
}
