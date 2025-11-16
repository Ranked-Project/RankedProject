package net.rankedproject.spigot.npc.executor.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.factory.NpcFactory;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcLoadListener implements Listener {

    private final NpcSpawnedTracker npcSpawnedTracker;

    @EventHandler
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Chunk chunk = event.getChunk();
        npcSpawnedTracker.getNpcList(playerUUID)
                .stream()
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().location().getChunk().equals(chunk))
                .forEach(loadedNpc -> loadedNpc.npc().getNpcSpawnExecutor().spawnEntity(loadedNpc, playerUUID));
    }

    @EventHandler
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var chunk = event.getChunk();
        npcSpawnedTracker.getNpcList(playerUUID).stream()
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().location().getChunk().equals(chunk))
                .forEach(loadedNpc -> loadedNpc.npc().getNpcSpawnExecutor().despawnEntity(loadedNpc, playerUUID));
    }
}
