package net.rankedproject.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import com.google.inject.Injector;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rankedproject.common.instantiator.Instantiator;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import net.rankedproject.common.packet.sender.PacketSenderImpl;
import net.rankedproject.common.packet.sender.data.PacketSendingData;
import net.rankedproject.common.registrar.AsyncRegistrar;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.common.rest.RestCrudAPI;
import net.rankedproject.common.util.ServerType;
import net.rankedproject.proto.SendPlayerToServer;
import net.rankedproject.proto.SpigotServerConnect;
import net.rankedproject.spigot.guice.PluginBinderModule;
import net.rankedproject.spigot.server.RankedServer;
import net.rankedproject.spigot.server.task.ServerPingTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
public abstract class CommonPlugin extends JavaPlugin {

    private final RankedServer rankedServer = rankedServer();
    private final UUID serverUUID = UUID.randomUUID();

    protected Injector injector;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        initGuice();

        initInstantiator();
        initRegistrars();
        connectServerVelocity();
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        var requiredPlayerData = rankedServer.requiredPlayerData();
        requiredPlayerData.forEach(clientType -> injector.getInstance(clientType).shutdown());

        var nats = injector.getInstance(NatsInstantiator.class).get();
        nats.close();

        RestCrudAPI.EXECUTOR_SERVICE.shutdown();
    }

    private void initGuice() {
        PluginBinderModule module = new PluginBinderModule(this);
        injector = module.createInjector();
        injector.injectMembers(this);
    }

    private void initRegistrars() {
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

            if (registrar.getPriority() != ExecutionPriority.FIRST) {
                processingChain = processingChain.thenRunAsync(registrar::register, mainThreadExecutor);
            }
        }
    }

    private void initInstantiator() {
        rankedServer.instantiator()
                .stream()
                .map(registrar -> injector.getInstance(registrar))
                .forEach(Instantiator::init);
    }

    private void connectServerVelocity() {
        log.info("Connecting to server at {}", serverUUID);
        var packet = SpigotServerConnect.newBuilder()
                .setHostName("127.0.0.1")
                .setPort(Bukkit.getPort())
                .setServerUuid(serverUUID.toString())
                .setServerType(rankedServer.serverType().toString())
                .setMaxPlayers(100)
                .build();

        injector.getInstance(PacketSenderImpl.class)
                .builder("spigot.server.connect", packet)
                .send()
                .whenComplete((sender, throwable) -> {
                    if (throwable != null) {
                        log.error("Error connecting to server", throwable);
                        return;
                    }

                    ServerPingTask.runTask(injector);
                    log.info("Server connected to server: {}", sender);
                });
    }

    @NotNull
    protected abstract RankedServer rankedServer();
}