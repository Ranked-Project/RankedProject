package net.rankedproject.gameapi.world;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.rankedproject.gameapi.Game;
import net.rankedproject.spigot.world.loader.WorldLoaderType;
import net.rankedproject.spigot.world.loader.WorldNamingStrategy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class GameWorldContext {

    private static final WeakReference<World> NOT_LOADED_WORLD = new WeakReference<>(null);
    private final Game game;

    private WeakReference<World> world = NOT_LOADED_WORLD;

    public @NotNull CompletableFuture<Void> load() {
        var gameMetadata = game.getMetadata();
        var worldName = gameMetadata.getWorldName();

        return WorldLoaderType.SLIME_WORLD
                .getLoader()
                .load(game.getPlugin(), worldName, WorldNamingStrategy.RANDOM_UUID_NAME)
                .thenAccept(loadedWorld -> world = new WeakReference<>(loadedWorld));
    }

    public void unload() {
        var bukkitWorld = getWorld();
        Preconditions.checkNotNull(bukkitWorld, "unloading world is null");

        Bukkit.unloadWorld(bukkitWorld, false);
    }

    public @Nullable World getWorld() {
        return world.get();
    }
}
