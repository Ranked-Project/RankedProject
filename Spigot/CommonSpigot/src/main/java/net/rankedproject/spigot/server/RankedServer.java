package net.rankedproject.spigot.server;

import net.rankedproject.common.config.Config;
import net.rankedproject.common.rest.type.PlayerRestClient;
import net.rankedproject.spigot.instantiator.Instantiator;
import net.rankedproject.spigot.registrar.Registrar;
import net.rankedproject.spigot.world.Spawn;

import java.util.Collection;

public record RankedServer(
        Collection<Class<? extends Instantiator<?>>> instantiator,
        Collection<Class<? extends Registrar>> registrars,
        Collection<Class<? extends PlayerRestClient<?>>> requiredPlayerData,
        Collection<Class<? extends Config>> configs,
        Spawn spawn,
        String name
) {
}
