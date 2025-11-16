package net.rankedproject.spigot.npc.click;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class NpcClickBehavior {

    private final NpcClickBehaviorMetadata behavior;

    public NpcClickBehavior() {
        this.behavior = behavior();
    }

    @NotNull
    public abstract NpcClickBehaviorMetadata behavior();
}