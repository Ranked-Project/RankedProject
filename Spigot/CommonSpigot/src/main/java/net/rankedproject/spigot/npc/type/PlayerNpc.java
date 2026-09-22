package net.rankedproject.spigot.npc.type;

import com.google.inject.Injector;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.executor.impl.PlayerNpcSpawnExecutor;
import net.rankedproject.spigot.npc.executor.impl.MobNpcSpawnExecutor;
import net.rankedproject.spigot.npc.model.impl.EntityNpcModel;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerNpc extends Npc {

    public PlayerNpc(@NotNull Injector injector) {
        super(injector);
    }

    @NotNull
    @Override
    public NpcSpawnExecutor getNpcSpawnExecutor() {
        if (behavior.model() instanceof EntityNpcModel) {
            return injector.getInstance(MobNpcSpawnExecutor.class);
        }
        return injector.getInstance(PlayerNpcSpawnExecutor.class);
    }
}
