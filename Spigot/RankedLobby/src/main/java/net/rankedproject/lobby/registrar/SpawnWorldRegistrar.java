package net.rankedproject.lobby.registrar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.registrar.AsyncRegistrar;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import net.rankedproject.spigot.world.loader.WorldLoaderType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SpawnWorldRegistrar implements AsyncRegistrar {

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public CompletableFuture<?> registerAsync() {
        var rankedServer = plugin.getRankedServer();
        var spawn = rankedServer.spawn();

        var worldLoader = WorldLoaderType.SLIME_WORLD.getLoader();
        return worldLoader.load(plugin, spawn.getWorldName());
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.LOADED_DEFAULTS;
    }
}
