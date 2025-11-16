package net.rankedproject.spigot.npc.executor;

import com.google.inject.Singleton;
import net.rankedproject.spigot.npc.Npc;

import java.util.UUID;

@Singleton
public interface NpcSpawnExecutor {

    void spawnEntity(LoadedNpc npc, UUID playerUUID);

    void despawnEntity(LoadedNpc npc, UUID playerUUID);
}