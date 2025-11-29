package net.rankedproject.spigot.server;

import com.google.inject.AbstractModule;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.instantiator.Instantiator;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.common.registrar.impl.PacketListenerRegistrar;
import net.rankedproject.common.rest.type.PlayerRestClient;
import net.rankedproject.common.util.ServerType;
import net.rankedproject.spigot.command.RankedCommand;
import net.rankedproject.spigot.instantiator.CommandManagerInstantiator;
import net.rankedproject.spigot.instantiator.SlimeLoaderInstantiator;
import net.rankedproject.spigot.registrar.*;
import net.rankedproject.spigot.world.Spawn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RankedServerBuilder {

    private final List<Class<? extends Registrar>> registrars = new ArrayList<>(Arrays.asList(
            ConfigRegistrar.class,
            ServerProxyRegistrar.class,
            BukkitListenerRegistrar.class,
            PacketListenerRegistrar.class,
            CommandRegistrar.class,
            AutoSpawnNpcRegistrar.class
    ));

    private final List<Class<? extends Instantiator<?>>> instantiators = new ArrayList<>(Arrays.asList(
            NatsInstantiator.class,
            SlimeLoaderInstantiator.class,
            CommandManagerInstantiator.class
    ));

    private final List<Class<? extends PlayerRestClient<?>>> requiredPlayerData = new ArrayList<>();
    private final List<Class<? extends RankedCommand>> ignoredCommands = new ArrayList<>();
    private final List<Class<? extends Config>> configs = new ArrayList<>();
    private final List<AbstractModule> modules = new ArrayList<>();

    private Spawn spawn;
    private String name;
    private ServerType serverType;

    @NotNull
    public RankedServerBuilder setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    @NotNull
    public RankedServerBuilder addRegistrar(@NotNull Class<? extends Registrar> registrar) {
        this.registrars.add(registrar);
        return this;
    }

    @NotNull
    public RankedServerBuilder addRegistrar(@NotNull Collection<Class<? extends Registrar>> registrars) {
        this.registrars.addAll(registrars);
        return this;
    }

    @NotNull
    public RankedServerBuilder addRequiredPlayerData(@NotNull Class<? extends PlayerRestClient<?>> requiredPlayerDataClass) {
        this.requiredPlayerData.add(requiredPlayerDataClass);
        return this;
    }

    @NotNull
    public RankedServerBuilder addRequiredPlayerData(@NotNull Collection<Class<? extends PlayerRestClient<?>>> requiredPlayerDataClasses) {
        this.requiredPlayerData.addAll(requiredPlayerDataClasses);
        return this;
    }

    @NotNull
    public RankedServerBuilder addInstantiator(@NotNull Class<? extends Instantiator<?>> instantiator) {
        this.instantiators.add(instantiator);
        return this;
    }

    @NotNull
    public RankedServerBuilder addInstantiator(@NotNull Collection<Class<? extends Instantiator<?>>> instantiator) {
        this.instantiators.addAll(instantiator);
        return this;
    }

    @NotNull
    public RankedServerBuilder addConfig(@NotNull Class<? extends Config> config) {
        this.configs.add(config);
        return this;
    }

    @NotNull
    public RankedServerBuilder addIgnoredCommand(@NotNull Class<? extends RankedCommand> command) {
        this.ignoredCommands.add(command);
        return this;
    }

    @NotNull
    public RankedServerBuilder addIgnoredCommands(@NotNull Collection<? extends Class<? extends RankedCommand>> commands) {
        this.ignoredCommands.addAll(commands);
        return this;
    }

    @NotNull
    public RankedServerBuilder addSpawn(@NotNull Spawn spawn) {
        this.spawn = spawn;
        return this;
    }

    @NotNull
    public RankedServerBuilder addModule(@NotNull AbstractModule module) {
        modules.add(module);
        return this;
    }

    @NotNull
    public RankedServerBuilder setServerType(ServerType serverType) {
        this.serverType = serverType;
        return this;
    }

    @NotNull
    public RankedServer build() {
        return new RankedServer(
                instantiators,
                registrars,
                requiredPlayerData,
                configs,
                ignoredCommands,
                modules,
                spawn,
                name,
                serverType
        );
    }
}