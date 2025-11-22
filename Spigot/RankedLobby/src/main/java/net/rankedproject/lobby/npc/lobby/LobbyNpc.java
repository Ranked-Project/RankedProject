package net.rankedproject.lobby.npc.lobby;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.NpcBehavior;
import net.rankedproject.spigot.npc.type.MobNpc;
import net.rankedproject.spigot.npc.type.PlayerNpc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Singleton
public class LobbyNpc extends MobNpc {

    @Inject
    public LobbyNpc(@NotNull Injector injector) {
        super(injector);
    }
    
    @Override
    public @NotNull NpcBehavior behavior() {
        return NpcBehavior.builder()
                .location(new Location(Bukkit.getWorld("lobby"), 27.5, 91, 17.5))
                .clickBehavior(injector.getInstance(LobbyNpcClickBehavior.class))
                .entityType(EntityType.BAT)
                .autoSpawn()
                .build();
    }
}