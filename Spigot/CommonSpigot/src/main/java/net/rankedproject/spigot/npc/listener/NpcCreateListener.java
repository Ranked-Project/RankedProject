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
import org.bukkit.event.server.PluginDisableEvent;
import net.rankedproject.spigot.CommonPlugin;
import org.bukkit.Bukkit;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcCreateListener implements Listener {

    private final AutoSpawnNpcRegistry autoSpawnNpcRegistry;
    private final NpcSpawnedTracker npcSpawnedTracker;
    private final NpcFactory npcFactory;
    private final CommonPlugin plugin;

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == plugin) {
            Bukkit.getOnlinePlayers().forEach(player -> removeNpcs(player.getUniqueId()));
        }
    }

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

        removeNpcs(playerUUID);
    }

    private void removeNpcs(java.util.UUID playerUUID) {
        var npcList = npcSpawnedTracker.getNpcMap(playerUUID).values().stream().toList();
        npcList.forEach(loadedNpc -> npcFactory.remove(playerUUID, loadedNpc.entityId()));
    }
}
