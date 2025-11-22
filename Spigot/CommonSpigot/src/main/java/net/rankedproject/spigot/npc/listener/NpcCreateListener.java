package net.rankedproject.spigot.npc.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import net.rankedproject.spigot.npc.factory.NpcFactory;
import net.rankedproject.spigot.npc.registry.AutoSpawnNpcRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcCreateListener implements Listener {

    private final AutoSpawnNpcRegistry autoSpawnNpcRegistry;
    private final NpcSpawnedTracker npcSpawnedTracker;
    private final NpcFactory npcFactory;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var registered = autoSpawnNpcRegistry.getAllRegistered();
        registered.forEach((npcType, _) -> npcFactory.create(playerUUID, npcType));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var npcIterator = npcSpawnedTracker.getNpcMap(playerUUID).values().iterator();
        while (npcIterator.hasNext()) {
            npcIterator.remove();

            var loadedNpc = npcIterator.next();
            npcFactory.remove(playerUUID, loadedNpc);
        }
    }
}