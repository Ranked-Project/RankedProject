package net.rankedproject.skywars.metadata;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.gameapi.config.MapInfoConfig;
import net.rankedproject.gameapi.metadata.GameMetadataParser;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_={@Inject})
public class RankedGameMetadataParser implements GameMetadataParser<RankedGameMetadata> {

    private final Injector injector;

    @NotNull
    @Override
    public RankedGameMetadata parse(@NotNull String gameIdentifier) {
        var section = ConfigProvider.get(MapInfoConfig.class, injector)
                .path("games.%s".formatted(gameIdentifier))
                .getAsSection();

        var worldName = section.path("world-name").getAsString();
        var displayName = section.path("display-name").getAsString();
        var locations = section.path("locations").getAsList(Location.class);

        return new RankedGameMetadata(worldName, displayName, gameIdentifier, locations);
    }
}