package net.rankedproject.lobby.npc.penkay;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.lobby.npc.lobby.LobbyNpcClickBehavior;
import net.rankedproject.spigot.npc.NpcBehavior;
import net.rankedproject.spigot.npc.type.MobNpc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PenkayNpc extends MobNpc {

    @Inject
    public PenkayNpc(@NotNull Injector injector) {
        super(injector);
    }

    @Override
    @NotNull
    protected NpcBehavior behavior() {
        return NpcBehavior.builder()
                .location(new Location(Bukkit.getWorld("lobby"), 31, 91, 17))
                .entityType(EntityType.BEE)
                .clickBehavior(injector.getInstance(LobbyNpcClickBehavior.class))
                .autoSpawn()
                .build();
    }
}
