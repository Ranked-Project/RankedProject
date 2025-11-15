package net.rankedproject.skywars.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.game.factory.GameFactory;
import net.rankedproject.skywars.RankedSkywarsGame;
import net.rankedproject.skywars.metadata.RankedGameMetadata;
import net.rankedproject.skywars.metadata.RankedGameMetadataParser;
import net.rankedproject.spigot.CommonPlugin;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RankedSkywarsGameFactory implements GameFactory<RankedSkywarsGame, RankedGameMetadata> {

    @NotNull
    @Override
    public Class<RankedGameMetadataParser> getParser() {
        return RankedGameMetadataParser.class;
    }

    @NotNull
    @Override
    public RankedSkywarsGame createInstance(@NotNull CommonPlugin plugin, @NotNull RankedGameMetadata metadata) {
        return new RankedSkywarsGame(plugin, metadata);
    }
}
