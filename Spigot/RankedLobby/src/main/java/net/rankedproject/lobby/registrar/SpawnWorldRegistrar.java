package net.rankedproject.lobby.registrar;

import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.registrar.PluginRegistrar;
import net.rankedproject.spigot.world.loader.WorldLoaderType;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class SpawnWorldRegistrar implements PluginRegistrar {

    private final CommonPlugin plugin;

    @Override
    public CompletableFuture<?> register() {
        var rankedServer = plugin.getRankedServer();
        var spawn = rankedServer.spawn();

        var worldLoader = WorldLoaderType.SLIME_WORLD.getLoader();
        return worldLoader.load(plugin, spawn.getWorldName());
    }
}