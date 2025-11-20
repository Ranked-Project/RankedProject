package net.rankedproject.lobby;

import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.lobby.config.LobbyConfig;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.world.Spawn;
import net.rankedproject.spigot.world.SpawnFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class LobbySpawn implements Spawn {

    private static final Set<SpawnFlag> SPAWN_FLAGS = Set.of(
            SpawnFlag.NO_PVP,
            SpawnFlag.NO_TELEPORT,
            SpawnFlag.NO_DESTROY_CROP,
            SpawnFlag.NO_RIGHT_CLICK,
            SpawnFlag.NO_FOOD_CONSUME,
            SpawnFlag.NO_BLOCK_PLACE,
            SpawnFlag.NO_BLOCK_BREAK,
            SpawnFlag.NO_ENTITY_DAMAGE,
            SpawnFlag.NO_CREATURE_SPAWN,
            SpawnFlag.NO_PROJECTILE,
            SpawnFlag.AUTO_TELEPORT
    );

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public String getWorldName() {
        return ConfigProvider.get(LobbyConfig.class, plugin.getInjector())
                .path("spawn.location.world-name")
                .getAsString();
    }

    @NotNull
    @Override
    public Function<Player, Location> getLocationFinder() {
        return _ -> ConfigProvider.get(LobbyConfig.class, plugin.getInjector())
                .path("spawn.location")
                .get(Location.class);
    }

    @NotNull
    @Override
    public Set<SpawnFlag> getFlags() {
        return SPAWN_FLAGS;
    }
}
