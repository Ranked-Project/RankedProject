package net.rankedproject.spigot.npc.click.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NpcClickListener implements PacketListener {

    private static final long FLOOD_DELAY_MS = 1000L;

    private final Object2LongMap<@NotNull UUID> floodCache = new Object2LongOpenHashMap<>();
    private final NpcSpawnedTracker npcSpawnedTracker;

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;

        var playerUUID = event.getUser().getUUID();
        int entityId = new WrapperPlayClientInteractEntity(event).getEntityId();

        var loadedNpc = getClickedNpc(playerUUID, entityId);
        if (loadedNpc == null) return;
        if (isFlooding(playerUUID)) return;

        var onClickAction = loadedNpc.npc().getBehavior().clickBehavior().behavior().onClick();
        var player = Bukkit.getPlayer(playerUUID);

        onClickAction.accept(player);
        floodCache.put(playerUUID, System.currentTimeMillis());
    }

    @Nullable
    private LoadedNpc getClickedNpc(@NotNull UUID playerUUID, int entityId) {
        return npcSpawnedTracker.getNpcList(playerUUID)
                .stream()
                .filter(loadedNpc -> loadedNpc.entityId() == entityId)
                .filter(loadedNpc -> loadedNpc.npc().getBehavior().clickBehavior() != null)
                .findFirst()
                .orElse(null);
    }

    private boolean isFlooding(@NotNull UUID playerUUID) {
        long lastTimeStamp = floodCache.getLong(playerUUID);
        long calculatedTime = System.currentTimeMillis() - lastTimeStamp;

        return calculatedTime < FLOOD_DELAY_MS;
    }
}