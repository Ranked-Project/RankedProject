package net.rankedproject.spigot.npc;

import com.google.inject.Injector;
import lombok.Getter;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public abstract class Npc {

    protected final NpcBehavior behavior;
    protected final Injector injector;

    public Npc(@NotNull Injector injector) {
        this.injector = injector;
        this.behavior = this.behavior();
    }

    public abstract NpcSpawnExecutor getNpcSpawnExecutor();

    @NotNull
    public abstract NpcBehavior behavior();
}