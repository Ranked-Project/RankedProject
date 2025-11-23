package net.rankedproject.lobby.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.tracker.EntityTracker;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();

        BetterModel.limb("demon_knight")
                .map(limb -> limb.getOrCreate(player))
                .ifPresent(tracker -> {
                    tracker.show(player);
                    tracker.animate("idle", AnimationModifier.DEFAULT);
                });
    }
}