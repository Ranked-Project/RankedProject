package net.rankedproject.skywars.metadata;

import net.rankedproject.gameapi.metadata.GameMetadata;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RankedGameMetadata(String worldName, String displayName, String identifier, List<Location> spawnLocations) implements GameMetadata {

    @NotNull
    @Override
    public String getWorldName() {
        return worldName;
    }

    @NotNull
    @Override
    public String getGameDisplayName() {
        return displayName;
    }

    @NotNull
    @Override
    public String getGameIdentifier() {
        return identifier;
    }

    @NotNull
    @Override
    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }
}