package net.rankedproject.common.rest.type;

import net.rankedproject.common.data.domain.BasePlayer;
import net.rankedproject.common.rest.request.RequestFactory;
import net.rankedproject.common.rest.request.type.RequestContent;
import net.rankedproject.common.rest.request.type.RequestType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public abstract class PlayerRestClient<V extends BasePlayer> extends CrudRestClient<V> {

    private static final int MAX_UPDATE_RETRY_ATTEMPTS = 10;
    private static final String SAVE_RETRIES_FAILED = "Couldn't save data for user uuid %s, attempted %s times. %s";

    protected PlayerRestClient(final @NotNull RequestFactory requestFactory) {
        super(requestFactory);
    }

    public @NotNull Collection<V> getAllPlayers() {
        return getAll();
    }

    public @NotNull V getPlayer(final @NotNull UUID key) {
        return get(key.toString());
    }

    public void updatePlayer(
            final @NotNull UUID key,
            final @NotNull V value,
            final @NotNull Consumer<V> dataAction
    ) {
        updatePlayerWithRetries(key, value, dataAction, 1);
    }

    private void updatePlayerWithRetries(
            final @NotNull UUID key,
            final @NotNull V value,
            final @NotNull Consumer<V> dataAction,
            final int attemptedTimes
    ) {
        if (attemptedTimes > MAX_UPDATE_RETRY_ATTEMPTS) {
            LOGGER.severe(() -> SAVE_RETRIES_FAILED.formatted(key, attemptedTimes, GSON.toJson(value)));
            return;
        }

        V retrievedValue = getPlayer(key);
        dataAction.accept(retrievedValue);

        try (Response response = sendRequest(RequestType.PUT, RequestContent.builder()
                .requestBuilder(builder -> builder.put(RequestBody.create(GSON.toJson(retrievedValue), JSON)))
                .build())) {

            if (!response.isSuccessful()) {
                updatePlayerWithRetries(key, retrievedValue, dataAction, attemptedTimes + 1);
            }
        }
    }

    public void savePlayer(final @NotNull V value) {
        save(value);
    }

    public @NotNull CompletableFuture<V> getPlayerAsync(final @NotNull UUID playerUUID) {
        return async(() -> getPlayer(playerUUID));
    }

    public @NotNull CompletableFuture<Collection<V>> getAllPlayersAsync() {
        return async(this::getAllPlayers);
    }

    public @NotNull CompletableFuture<Void> updatePlayerAsync(
            final @NotNull UUID key,
            final @NotNull V data,
            final @NotNull Consumer<V> dataAction
    ) {
        return async(() -> updatePlayer(key, data, dataAction));
    }

    public @NotNull CompletableFuture<Void> savePlayerAsync(V data) {
        return async(() -> savePlayer(data));
    }
}
