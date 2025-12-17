package net.rankedproject.game.factory;

import net.rankedproject.gameapi.Game;
import net.rankedproject.gameapi.metadata.GameMetadata;
import net.rankedproject.gameapi.metadata.GameMetadataParser;
import net.rankedproject.spigot.CommonPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface GameFactory<G extends Game, M extends GameMetadata> {

    /**
     * Returns the parser used to turn a game identifier into metadata.
     *
     * @return the metadata parser
     */
    @NotNull Class<? extends GameMetadataParser<M>> getParser();

    /**
     * Builds a new {@link Game} from the given metadata and plugin.
     * <p>
     * Note: this just creates the object, it doesn’t load the world yet.
     * Use {@link #create(CommonPlugin, String)} if you also want world
     * loading handled for you.
     *
     * @param plugin   the plugin context
     * @param metadata info that describes the game
     * @return a new game instance (not yet loaded)
     */
    @NotNull G createInstance(@NotNull CommonPlugin plugin, @NotNull M metadata);

    /**
     * Convenience method for creating and initializing a game in one go.
     * <p>
     * This will:
     * <ol>
     *   <li>Parse the metadata for the given {@code gameIdentifier}.</li>
     *   <li>Create the game object.</li>
     *   <li>Asynchronously load its world context.</li>
     * </ol>
     *
     * @param plugin         the plugin context
     * @param gameIdentifier unique identifier for the game
     * @return a future that completes once the game and its world are ready
     */
    default @NotNull CompletableFuture<G> create(@NotNull CommonPlugin plugin, @NotNull String gameIdentifier) {
        var metadataParser = plugin.getInjector().getInstance(getParser());
        var metadata = metadataParser.parse(gameIdentifier);

        G game = createInstance(plugin, metadata);
        return game.getWorldContext()
                .load()
                .thenApplyAsync(_ -> game, Bukkit.getScheduler().getMainThreadExecutor(plugin));
    }
}
