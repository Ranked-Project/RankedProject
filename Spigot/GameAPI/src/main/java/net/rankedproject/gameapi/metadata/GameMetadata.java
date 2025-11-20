package net.rankedproject.gameapi.metadata;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GameMetadata {

    @NotNull
    String getWorldName();

    @NotNull
    String getGameDisplayName();

    @NotNull
    String getGameIdentifier();

    @NotNull
    List<Location> getSpawnLocations();
}