package net.rankedproject.common.rest.type;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.rankedproject.common.data.domain.BasePlayer;
import net.rankedproject.common.rest.request.RequestFactory;
import net.rankedproject.common.rest.request.type.RequestContent;
import net.rankedproject.common.rest.request.type.RequestType;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("UnusedReturnValue")
public abstract class PlayerRestClient<V extends BasePlayer> extends CrudRestClient<V> {

    private static final int MAX_UPDATE_RETRY_ATTEMPTS = 10;

    public PlayerRestClient(RequestFactory requestFactory) {
        super(requestFactory);
    }

    public Collection<V> getAllPlayers() {
        return getAll();
    }

    public V getPlayer(UUID key) {
        return get(key.toString());
    }

    public void updatePlayer(UUID key, V value, Consumer<V> dataAction) {
        updatePlayerWithRetries(key, value, dataAction, 1);
    }

    private void updatePlayerWithRetries(UUID key, V value, Consumer<V> dataAction, int attemptedTimes) {
        if (attemptedTimes > MAX_UPDATE_RETRY_ATTEMPTS) {
            LOGGER.severe("Couldn't save data for user uuid %s, attempted %s times. %s".formatted(key, attemptedTimes, GSON.toJson(value)));
            return;
        }

        V retrievedValue = getPlayer(key);
        dataAction.accept(retrievedValue);

        try (Response response = sendRequest(RequestType.PUT, RequestContent.builder()
                .requestBuilder(builder -> builder.put(RequestBody.create(GSON.toJson(retrievedValue), JSON)))
                .build())) {

            if (response.isSuccessful()) return;
            updatePlayerWithRetries(key, retrievedValue, dataAction, attemptedTimes + 1);
        }
    }

    public void savePlayer(V value) {
        save(value);
    }

    public CompletableFuture<V> getPlayerAsync(UUID playerUUID) {
        return async(() -> getPlayer(playerUUID));
    }

    public CompletableFuture<Collection<V>> getAllPlayersAsync(UUID playerUUID) {
        return async(this::getAllPlayers);
    }

    public CompletableFuture<Void> updatePlayerAsync(UUID key, V data, Consumer<V> dataAction) {
        return async(() -> updatePlayer(key, data, dataAction));
    }

    public CompletableFuture<Void> savePlayerAsync(V data) {
        return async(() -> savePlayer(data));
    }
}
