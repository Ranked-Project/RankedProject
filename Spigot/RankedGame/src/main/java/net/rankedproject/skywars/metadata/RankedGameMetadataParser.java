package net.rankedproject.skywars.metadata;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.gameapi.config.MapInfoConfig;
import net.rankedproject.gameapi.metadata.GameMetadataParser;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_={@Inject})
public class RankedGameMetadataParser implements GameMetadataParser<RankedGameMetadata> {

    private final Injector injector;

    @NotNull
    @Override
    public RankedGameMetadata parse(@NotNull String gameIdentifier) {
        var worldName = ConfigProvider.get(MapInfoConfig.class, injector)
                .path("games.%s.world-name".formatted(gameIdentifier))
                .getAsString();

        var displayName = ConfigProvider.get(MapInfoConfig.class, injector)
                .path("games.%s.display-name")
                .getAsString();

        return new RankedGameMetadata(worldName, displayName, gameIdentifier);
    }
}