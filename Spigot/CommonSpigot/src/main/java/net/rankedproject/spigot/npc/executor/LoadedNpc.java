package net.rankedproject.spigot.npc.executor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import kr.toxicity.model.api.tracker.DummyTracker;
import net.rankedproject.spigot.npc.Npc;

@Getter
@Accessors(fluent = true)
public final class LoadedNpc {
    private final int entityId;
    private final Npc npc;

    // Each LoadedNpc belongs to one viewer; model visibility follows the same lifecycle.
    @Setter
    private volatile DummyTracker modelTracker;

    @Builder
    public LoadedNpc(int entityId, Npc npc) {
        this.entityId = entityId;
        this.npc = npc;
    }
}
