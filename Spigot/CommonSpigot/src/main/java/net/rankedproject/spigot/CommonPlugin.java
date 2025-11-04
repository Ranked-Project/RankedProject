package net.rankedproject.spigot;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.spigot.guice.PluginBinderModule;
import net.rankedproject.spigot.instantiator.InstantiatorRegistry;
import net.rankedproject.spigot.instantiator.impl.SlimeLoaderInstantiator;
import net.rankedproject.spigot.registrar.AsyncRegistrar;
import net.rankedproject.spigot.server.RankedServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
public abstract class CommonPlugin extends JavaPlugin {

    private final RankedServer rankedServer = rankedServer();

    private InstantiatorRegistry instantiatorRegistry;
    private Injector injector;

    @Override
    public void onEnable() {
        initGuice();

        initInstantiator(rankedServer);
        initRegistrars(rankedServer);
    }

    @Override
    public void onDisable() {
        var requiredPlayerData = rankedServer.requiredPlayerData();
        requiredPlayerData.forEach(clientType -> injector.getInstance(clientType).shutdown());
    }

    private void initGuice() {
        PluginBinderModule module = new PluginBinderModule(this);
        injector = module.createInjector();
        injector.injectMembers(this);
    }

    private void initRegistrars(@NotNull RankedServer rankedServer) {
        var mainThreadExecutor = Bukkit.getScheduler().getMainThreadExecutor(this);
        var registrars = rankedServer.registrars().stream()
                .map(registrar -> injector.getInstance(registrar))
                .sorted(Comparator.comparingInt(registrar -> registrar.getPriority().ordinal()))
                .toList();

        CompletableFuture<?> processingChain = CompletableFuture.completedFuture(null);
        for (var registrar : registrars) {
            if (registrar instanceof AsyncRegistrar asyncRegistrar) {
                processingChain = processingChain.thenCompose(_ -> asyncRegistrar.registerAsync());
                continue;
            }

            processingChain = processingChain.thenRunAsync(registrar::register, mainThreadExecutor);
        }
    }

    @SuppressWarnings("unchecked")
    private void initInstantiator(RankedServer rankedServer) {
        instantiatorRegistry = new InstantiatorRegistry();
        instantiatorRegistry.register(SlimeLoaderInstantiator.class, new SlimeLoaderInstantiator());

        rankedServer.instantiator()
                .stream()
                .map(registrar -> injector.getInstance(registrar))
                .forEach(loader -> instantiatorRegistry.register(loader.getClass(), loader));

        var registeredInstantiators = instantiatorRegistry.getAll();
        registeredInstantiators.forEach(loader -> {
            var loadedData = loader.init();
            Preconditions.checkNotNull(loadedData);
        });
    }

    @NotNull
    protected abstract RankedServer rankedServer();
}