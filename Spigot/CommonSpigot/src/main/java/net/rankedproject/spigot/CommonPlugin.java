package net.rankedproject.spigot;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.spigot.guice.PluginBinderModule;
import net.rankedproject.spigot.instantiator.Instantiator;
import net.rankedproject.spigot.registrar.AsyncRegistrar;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import net.rankedproject.spigot.registrar.Registrar;
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
    protected Injector injector;

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

        registrars.stream()
                .filter(registrar -> !(registrar instanceof AsyncRegistrar))
                .filter(registrar -> registrar.getPriority() == ExecutionPriority.FIRST)
                .forEach(Registrar::register);

        CompletableFuture<?> processingChain = CompletableFuture.completedFuture(null);
        for (var registrar : registrars) {
            if (registrar instanceof AsyncRegistrar asyncRegistrar) {
                processingChain = processingChain.thenCompose(_ -> asyncRegistrar.registerAsync());
                continue;
            }

            processingChain = processingChain.thenRunAsync(registrar::register, mainThreadExecutor);
        }
    }

    private void initInstantiator(@NotNull RankedServer rankedServer) {
        rankedServer.instantiator()
                .stream()
                .map(registrar -> injector.getInstance(registrar))
                .forEach(Instantiator::init);
    }

    @NotNull
    protected abstract RankedServer rankedServer();
}