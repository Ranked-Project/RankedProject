package net.rankedproject.spigot.npc.click.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import net.rankedproject.spigot.util.PacketUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcClickListener implements PacketListener {

    private static final Duration FLOOD_DELAY_DURATION = Duration.ofSeconds(1);

    private final Object2LongMap<@NotNull UUID> floodCache = new Object2LongOpenHashMap<>();
    private final NpcSpawnedTracker npcSpawnedTracker;

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) {
            return;
        }

        var playerUUID = event.getUser().getUUID();
        int entityId = PacketUtil.getEntityIdFromEvent(event);

        var loadedNpc = npcSpawnedTracker.getNpcById(playerUUID, entityId);
        if (loadedNpc == null) {
            return;
        }

        var npcClickBehavior = loadedNpc.npc().getBehavior().clickBehavior();
        if (npcClickBehavior == null || isFlooding(playerUUID)) {
            return;
        }

        var onClickAction = npcClickBehavior.behavior().onClick();
        var player = Bukkit.getPlayer(playerUUID);

        onClickAction.accept(player);
        floodCache.put(playerUUID, System.currentTimeMillis());
    }

    private boolean isFlooding(@NotNull UUID playerUUID) {
        long lastTimeStamp = floodCache.getLong(playerUUID);
        long calculatedTime = System.currentTimeMillis() - lastTimeStamp;

        return calculatedTime < FLOOD_DELAY_DURATION.toMillis();
    }
}