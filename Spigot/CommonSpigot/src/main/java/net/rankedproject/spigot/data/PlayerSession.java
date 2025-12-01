package net.rankedproject.spigot.data;

import com.google.inject.ImplementedBy;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.rankedproject.common.data.domain.BasePlayer;
import net.rankedproject.common.rest.type.PlayerRestClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ImplementedBy(PlayerSessionImpl.class)
public interface PlayerSession {

    /**
     * Loads user data into cache permanently until explicitly unloaded.
     * <p>
     * If a newer version of the data is detected in the database during an update operation,
     * the cache will be refreshed automatically.
     *
     * @param clientType The specific {@link PlayerRestClient} class representing the data type to load.
     * @param playerUUID The UUID of the target player.
     * @param <T>        The type of the data to be loaded.
     * @return A non-null {@link CompletableFuture} containing the loaded data instance.
     *         The future will complete with a nullable value if no data exists.
     */
    @NotNull
    <U extends BasePlayer, T extends PlayerRestClient<U>> CompletableFuture<U> load(
            @NotNull UUID playerUUID,
            @NotNull Class<T> clientType
    );

    /**
     * Loads multiple types of user data into cache permanently until explicitly unloaded.
     * <p>
     * If any of the data types have newer versions in the database, the cache will be refreshed automatically.
     *
     * @param clients    A collection of {@link PlayerRestClient} class types to load.
     * @param playerUUID The UUID of the target player.
     * @return A non-null {@link CompletableFuture} that completes when all data types are loaded.
     *         Individual results may still be null if no data exists for a given type.
     */
    @NotNull CompletableFuture<?> load(
            @NotNull Collection<Class<? extends PlayerRestClient<?>>> clients,
            @NotNull UUID playerUUID
    );

    /**
     * Retrieves the latest version of user data from the database.
     * <p>
     * Does not use or update the cache.
     *
     * @param playerUUID The UUID of the target player.
     * @param <T>        The type of the data to retrieve.
     * @return A non-null {@link CompletableFuture} containing the most recent data from the database.
     *         The result may be null if no data exists.
     */
    @NotNull
    <T extends BasePlayer, U extends PlayerRestClient<T>> CompletableFuture<T> get(
            @NotNull UUID playerUUID,
            @NotNull Class<U> clientType
    );

    /**
     * Retrieves the cached user data for the given player, if available.
     *
     * @param playerUUID The UUID of the target player.
     * @param <T>        The type of the data to retrieve.
     * @return The cached data instance, or {@code null} if no data is cached.
     */
    @Nullable
    <T extends BasePlayer> T getCached(
            @NotNull UUID playerUUID,
            @NotNull Class<T> dataType
    );

    /**
     * Updates a user's data in the database.
     * <p>
     * If a newer version of the data is stored in the database during the update,
     * the cache will be automatically refreshed with the latest data.
     *
     * @param playerUUID    The UUID of the target player.
     * @param dataClassType The {@link BasePlayer} class representing the data type to update.
     * @param dataConsumer  A {@link Consumer} that applies changes to the data before saving.
     * @param <T>           The type of the data being updated.
     * @return A non-null {@link CompletableFuture} containing the updated data instance.
     */
    @NotNull
    <T extends BasePlayer> CompletableFuture<Void> updateData(
            @NotNull UUID playerUUID,
            @NotNull Class<T> dataClassType,
            @NotNull Consumer<T> dataConsumer
    );

    /**
     * Unloads all cached data for the given player.
     * <p>
     * Typically called when a player leaves the server to free memory.
     *
     * @param playerUUID The UUID of the player whose data should be removed from cache.
     */
    void unload(@NotNull UUID playerUUID);
}
