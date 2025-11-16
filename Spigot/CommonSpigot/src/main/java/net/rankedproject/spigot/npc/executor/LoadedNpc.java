package net.rankedproject.spigot.npc.executor;

import lombok.Builder;
import net.rankedproject.spigot.npc.Npc;
import org.bukkit.entity.Entity;

@Builder
public record LoadedNpc(
        int entityId,
        Npc npc
) {}