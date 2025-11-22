package net.rankedproject.spigot.npc.type;

import com.google.inject.Injector;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.executor.impl.MobNpcSpawnExecutor;
import org.jetbrains.annotations.NotNull;

public abstract class MobNpc extends Npc {

    public MobNpc(@NotNull Injector injector) {
        super(injector);
    }

    @Override
    public NpcSpawnExecutor getNpcSpawnExecutor() {
        return injector.getInstance(MobNpcSpawnExecutor.class);
    }
}