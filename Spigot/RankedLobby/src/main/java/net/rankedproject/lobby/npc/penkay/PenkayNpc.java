package net.rankedproject.lobby.npc.penkay;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.NpcBehavior;
import net.rankedproject.spigot.npc.click.NpcClickBehaviorMetadata;
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
                .clickBehavior(NpcClickBehaviorMetadata.builder()
                        .onClick(player -> player.sendMessage("Suck my dick and massage my nuts"))
                        .build())
                .autoSpawn()
                .build();
    }
}
