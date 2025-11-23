package net.rankedproject.spigot.npc.executor;

import lombok.Builder;
import net.rankedproject.spigot.npc.Npc;

@Builder
public record LoadedNpc(
        int entityId,
        Npc npc
) {}