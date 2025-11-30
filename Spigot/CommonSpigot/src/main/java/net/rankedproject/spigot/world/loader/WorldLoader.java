package net.rankedproject.spigot.world.loader;

import net.rankedproject.spigot.CommonPlugin;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface WorldLoader {

    @NotNull
    CompletableFuture<World> load(@NotNull CommonPlugin plugin, @NotNull String worldName, @NotNull WorldNamingStrategy namingStrategy);
}
