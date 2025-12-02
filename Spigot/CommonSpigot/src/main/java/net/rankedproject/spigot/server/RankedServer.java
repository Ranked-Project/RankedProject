package net.rankedproject.spigot.server;

import com.google.inject.AbstractModule;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.instantiator.Instantiator;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.common.rest.type.PlayerRestClient;
import net.rankedproject.common.network.server.ServerType;
import net.rankedproject.spigot.command.RankedCommand;
import net.rankedproject.spigot.world.Spawn;

import java.util.Collection;
import java.util.UUID;

public record RankedServer(
        Collection<Class<? extends Instantiator<?>>> instantiator,
        Collection<Class<? extends Registrar>> registrars,
        Collection<Class<? extends PlayerRestClient<?>>> requiredPlayerData,
        Collection<Class<? extends Config>> configs,
        Collection<Class<? extends RankedCommand>> ignoredCommands,
        Collection<AbstractModule> modules,
        Spawn spawn,
        String name,
        ServerType serverType,
        String identifier
) {
}