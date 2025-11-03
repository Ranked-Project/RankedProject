package net.rankedproject.spigot;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.rest.provider.RestClientRegistry;
import net.rankedproject.spigot.guice.PluginBinderModule;
import net.rankedproject.spigot.instantiator.InstantiatorRegistry;
import net.rankedproject.spigot.instantiator.impl.SlimeLoaderInstantiator;
import net.rankedproject.spigot.registrar.BukkitListenerRegistrar;
import net.rankedproject.spigot.registrar.ConfigRegistrar;
import net.rankedproject.spigot.registrar.PluginRegistrar;
import net.rankedproject.spigot.registrar.ServerProxyRegistrar;
import net.rankedproject.spigot.server.RankedServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
        rankedServer.requiredPlayerData().forEach(clientType ->
                injector.getInstance(clientType).shutdown()
        );
    }

    private void initGuice() {
        PluginBinderModule module = new PluginBinderModule(this);
        injector = module.createInjector();
        injector.injectMembers(this);
    }

    private void initRegistrars(@NotNull RankedServer rankedServer) {
        var configRegistrar = new ConfigRegistrar(this);
        var bukkitListenerRegistrar = new BukkitListenerRegistrar(this);
        var serverProxyRegistrar = new ServerProxyRegistrar();

        bukkitListenerRegistrar.register();

        var mainThreadExecutor = Bukkit.getScheduler().getMainThreadExecutor(this);
        configRegistrar.register()
//                .thenRunAsync(bukkitListenerRegistrar::register, mainThreadExecutor)
                .thenComposeAsync(_ -> getDefinedRegistrars(rankedServer.registrars()), mainThreadExecutor)
                .thenRun(serverProxyRegistrar::register);
    }

    @NotNull
    private CompletableFuture<Void> getDefinedRegistrars(Collection<PluginRegistrar> registrars) {
        return CompletableFuture.allOf(registrars.stream()
                .map(PluginRegistrar::register)
                .toArray(CompletableFuture[]::new));
    }

    @SuppressWarnings("unchecked")
    private void initInstantiator(RankedServer rankedServer) {
        instantiatorRegistry = new InstantiatorRegistry();
        instantiatorRegistry.register(SlimeLoaderInstantiator.class, new SlimeLoaderInstantiator());

        var instantiator = rankedServer.instantiator();
        instantiator.forEach(loader -> instantiatorRegistry.register(loader.getClass(), loader));

        var registeredInstantiators = instantiatorRegistry.getAll();
        registeredInstantiators.forEach(loader -> {
            var loadedData = loader.init();
            Preconditions.checkNotNull(loadedData);
        });
    }

    @NotNull
    protected abstract RankedServer rankedServer();
}