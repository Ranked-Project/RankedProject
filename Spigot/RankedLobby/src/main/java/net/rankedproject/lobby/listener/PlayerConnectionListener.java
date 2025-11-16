package net.rankedproject.lobby.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import net.rankedproject.spigot.npc.factory.NpcFactory;
import net.rankedproject.lobby.npc.lobby.LobbyNpc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerConnectionListener implements Listener {

    private final NpcSpawnedTracker npcSpawnedTracker;
    private final NpcFactory npcFactory;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        npcFactory.create(LobbyNpc.class, playerUUID);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        var npcList = npcSpawnedTracker.getNpcList(playerUUID);
        new ArrayList<>(npcList).forEach(loadedNpc -> npcFactory.remove(loadedNpc.entityId(), playerUUID));
    }
}