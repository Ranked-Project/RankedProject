package net.rankedproject.spigot.npc.click;

import lombok.Builder;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Builder
public record NpcClickBehaviorMetadata(
    Consumer<? super Player> onClick
) {}
