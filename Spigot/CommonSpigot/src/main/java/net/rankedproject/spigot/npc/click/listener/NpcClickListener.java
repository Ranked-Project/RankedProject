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
import net.rankedproject.spigot.npc.executor.tracker.NpcSpawnedTracker;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

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

        var user = event.getUser();
        var entity = new WrapperPlayClientInteractEntity(event);

        int entityId = entity.getEntityId();
        var loadedNpc = npcSpawnedTracker.getNpcList(user.getUUID())
                .stream()
                .filter(it -> it.entityId() == entityId)
                .filter(it -> it.npc().getBehavior().clickBehavior() != null)
                .findFirst()
                .orElse(null);
        if (loadedNpc == null) return;
        if (loadedNpc.npc().getBehavior().clickBehavior() == null) return;

        long lastTimeStamp = floodCache.getLong(user.getUUID());
        if (lastTimeStamp + FLOOD_DELAY_MS > System.currentTimeMillis()) return;

        var onClickAction = loadedNpc.npc().getBehavior().clickBehavior().behavior().onClick();
        var player = Bukkit.getPlayer(user.getUUID());

        onClickAction.accept(player);
        floodCache.put(user.getUUID(), System.currentTimeMillis());
    }
}