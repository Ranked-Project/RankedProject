package net.rankedproject.gameapi.world;

import lombok.RequiredArgsConstructor;
import net.rankedproject.gameapi.Game;
import net.rankedproject.spigot.world.loader.WorldLoaderType;
import net.rankedproject.spigot.world.loader.WorldNamingStrategy;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class GameWorldContext {

    private static final WeakReference<World> NOT_LOADED_WORLD = new WeakReference<>(null);
    private final Game game;

    private WeakReference<World> world = NOT_LOADED_WORLD;

    public CompletableFuture<Void> load() {
        var gameMetadata = game.getMetadata();
        var worldName = gameMetadata.getWorldName();
        var worldLoader = WorldLoaderType.SLIME_WORLD.getLoader();

        return worldLoader.load(game.getPlugin(), worldName, WorldNamingStrategy.RANDOM_UUID_NAME).thenAccept(loadedWorld -> {
            world = new WeakReference<>(loadedWorld);
        });
    }

    public void unload() {
        Bukkit.unloadWorld(getWorld(), false);
    }

    public World getWorld() {
        return world.get();
    }
}