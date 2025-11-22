package net.rankedproject.spigot.npc.executor.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcLoadListener implements Listener {

    private final NpcSpawnedTracker npcSpawnedTracker;
    private final Injector injector;

    @EventHandler
    @SuppressWarnings("unchecked")
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Chunk chunk = event.getChunk();
        npcSpawnedTracker.getNpcMap(playerUUID)
                .values()
                .stream()
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().location().getChunk().equals(chunk))
                .forEach(loadedNpc -> {
                    var npcSpawnExecutorType = loadedNpc.npc().getNpcSpawnExecutorType();
                    var npcSpawnExecutor = (NpcSpawnExecutor<Npc>) injector.getInstance(npcSpawnExecutorType);

                    var castedLoadedNpc = (LoadedNpc<Npc>) loadedNpc;
                    npcSpawnExecutor.spawnEntity(castedLoadedNpc, playerUUID);
                });
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var chunk = event.getChunk();
        npcSpawnedTracker.getNpcMap(playerUUID)
                .values()
                .stream()
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().location().getChunk().equals(chunk))
                .forEach(loadedNpc -> {
                    var npcSpawnExecutorType = loadedNpc.npc().getNpcSpawnExecutorType();
                    var npcSpawnExecutor = (NpcSpawnExecutor<Npc>) injector.getInstance(npcSpawnExecutorType);

                    var castedLoadedNpc = (LoadedNpc<Npc>) loadedNpc;
                    npcSpawnExecutor.despawnEntity(castedLoadedNpc, playerUUID);
                });
    }
}
