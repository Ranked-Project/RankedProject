package net.rankedproject.spigot.npc;

import com.google.inject.Injector;
import lombok.Getter;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Npc {

    protected final NpcBehavior behavior;
    protected final Injector injector;

    public Npc(@NotNull Injector injector) {
        this.injector = injector;
        this.behavior = behavior();
    }

    public abstract Class<? extends NpcSpawnExecutor<? extends Npc>> getNpcSpawnExecutorType();

    @NotNull
    public abstract NpcBehavior behavior();
}