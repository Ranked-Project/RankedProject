package net.rankedproject.spigot.npc.type;

import com.google.inject.Injector;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.executor.impl.PlayerNpcSpawnExecutor;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerNpc extends Npc {

    public PlayerNpc(@NotNull Injector injector) {
        super(injector);
    }

    @Override
    public NpcSpawnExecutor getNpcSpawnExecutor() {
        return injector.getInstance(PlayerNpcSpawnExecutor.class);
    }
}