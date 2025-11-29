package net.rankedproject.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.instantiator.Instantiator;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import net.rankedproject.common.instantiator.impl.RedisInstantiator;
import net.rankedproject.common.registrar.AsyncRegistrar;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.common.registrar.impl.PacketListenerRegistrar;
import net.rankedproject.velocity.registrar.VelocityListenerRegistrar;
import net.rankedproject.velocity.server.ServerHealthCheckTask;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Slf4j
@Getter
@Plugin(id = "velocity-proxy", name = "VelocityProxy")
public class VelocityProxy {

    private static final List<Class<? extends Instantiator<?>>> INSTANTIATORS = List.of(
            RedisInstantiator.class,
            NatsInstantiator.class
    );

    private static final List<Class<? extends Registrar>> REGISTRARS = List.of(
            VelocityListenerRegistrar.class,
            PacketListenerRegistrar.class
    );

    private final ProxyServer proxyServer;
    private final Logger logger;

    private final Injector injector;

    @Inject
    public VelocityProxy(Injector injector, ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.injector = injector;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.initializeServer();
    }

    private void initializeServer() {
        initInstantiator();
        initRegistrars();

        ServerHealthCheckTask.runTask(injector);
        log.info("[VelocityProxy] Initialize Server");
    }

    private void initRegistrars() {
        var registrars = REGISTRARS.stream()
                .map(injector::getInstance)
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

            if (registrar.getPriority() != ExecutionPriority.FIRST) {
                processingChain = processingChain.thenRun(registrar::register);
            }
        }
    }

    private void initInstantiator() {
        INSTANTIATORS.stream()
                .map(injector::getInstance)
                .forEach(Instantiator::init);
    }
}