package net.rankedproject.spigot.world.loader;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import lombok.SneakyThrows;
import net.rankedproject.common.rest.RestCrudAPI;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.instantiator.impl.SlimeLoaderInstantiator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SlimeWorldLoader implements WorldLoader {

    @NotNull
    @Override
    public CompletableFuture<World> load(
            @NotNull CommonPlugin plugin,
            @NotNull String worldName,
            @NotNull WorldNamingStrategy namingStrategy
    ) {
        var slimePaper = AdvancedSlimePaperAPI.instance();
        var slimeLoader = plugin.getInjector()
                .getInstance(SlimeLoaderInstantiator.class)
                .get();

        var renamedWorldName = namingStrategy.renameWorld(worldName);
        return CompletableFuture
                .supplyAsync(() -> {
                    var slimeWorld = readSlimeWorld(worldName, slimePaper, slimeLoader);
                    if (renamedWorldName.equals(worldName)) {
                        return slimeWorld;
                    }

                    return slimeWorld.clone(renamedWorldName);
                }, RestCrudAPI.EXECUTOR_SERVICE)
                .thenApplyAsync(slimeWorld -> {
                    var loadedWorld = slimePaper.loadWorld(slimeWorld, true);
                    return loadedWorld.getBukkitWorld();
                }, Bukkit.getScheduler().getMainThreadExecutor(plugin));
    }

    @SneakyThrows
    private SlimeWorld readSlimeWorld(@NotNull String worldName, AdvancedSlimePaperAPI slimePaper, SlimeLoader slimeLoader) {
        return slimePaper.readWorld(slimeLoader, worldName, false, new SlimePropertyMap());
    }
}