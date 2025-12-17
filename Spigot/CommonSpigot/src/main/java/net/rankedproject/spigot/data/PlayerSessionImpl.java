package net.rankedproject.spigot.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.data.domain.BasePlayer;
import net.rankedproject.common.rest.provider.RestProvider;
import net.rankedproject.common.rest.type.PlayerRestClient;
import net.rankedproject.spigot.CommonPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerSessionImpl implements PlayerSession {

    private final Map<UUID, Set<BasePlayer>> cache = new ConcurrentHashMap<>();

    private final RestProvider restProvider;
    private final CommonPlugin plugin;

    @Override
    public <U extends BasePlayer, T extends PlayerRestClient<U>> @NotNull CompletableFuture<U> load(
            final @NotNull UUID playerUUID,
            final @NotNull Class<T> clientType
    ) {
        return restProvider.get(clientType)
                .getPlayerAsync(playerUUID)
                .thenApply(data -> {
                    setCachedData(data);
                    return data;
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> load(
            final @NotNull Collection<Class<? extends PlayerRestClient<?>>> clients,
            final @NotNull UUID playerUUID
    ) {
        return CompletableFuture.allOf(clients.stream()
                .map(client -> (CompletableFuture<?>) load(playerUUID, (Class) client))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public <T extends BasePlayer, U extends PlayerRestClient<T>> @NotNull CompletableFuture<T> get(
            final @NotNull UUID playerUUID,
            final @NotNull Class<U> clientType
    ) {
        return restProvider.get(clientType).getPlayerAsync(playerUUID);
    }

    @Override
    public <T extends BasePlayer> @Nullable T getCached(
            final @NotNull UUID playerUUID,
            final @NotNull Class<T> dataType
    ) {
        return cache.getOrDefault(playerUUID, Collections.emptySet()).stream()
                .filter(data -> data.getClass() == dataType)
                .findFirst()
                .map(dataType::cast)
                .orElse(null);
    }

    @Override
    public <T extends BasePlayer> @NotNull CompletableFuture<Void> updateData(
            final @NotNull UUID playerUUID,
            final @NotNull Class<T> dataClassType,
            final @NotNull Consumer<T> dataAction
    ) {
        var restClientTypes = plugin.getRankedServer().requiredPlayerData();
        PlayerRestClient<T> client = restProvider.getByReturnType(dataClassType, restClientTypes);

        T cachedData = getCached(playerUUID, client.getReturnType());
        return client.updatePlayerAsync(playerUUID, cachedData, dataAction);
    }

    @Override
    public void unload(final @NotNull UUID playerUUID) {
        cache.remove(playerUUID);
    }

    private void setCachedData(final @NotNull BasePlayer basePlayer) {
        UUID playerId = basePlayer.getId();
        Set<BasePlayer> existingCache = cache.getOrDefault(playerId, new HashSet<>());

        existingCache.removeIf(data -> data.getClass().isAssignableFrom(basePlayer.getClass()));
        existingCache.add(basePlayer);

        cache.put(playerId, existingCache);
    }
}
