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

    @NotNull
    @Override
    public <U extends BasePlayer, T extends PlayerRestClient<U>> CompletableFuture<U> load(
            @NotNull UUID playerUUID,
            @NotNull Class<T> clientType
    ) {
        return restProvider.get(clientType)
                .getPlayerAsync(playerUUID)
                .thenApply(data -> {
                    setCachedData(data);
                    return data;
                });
    }

    @NotNull
    @Override
    public CompletableFuture<?> load(
            @NotNull Collection<Class<? extends PlayerRestClient<?>>> clients,
            @NotNull UUID playerUUID
    ) {
        return CompletableFuture.allOf(clients.stream()
                .map(client -> (CompletableFuture<?>) load(playerUUID, (Class) client))
                .toArray(CompletableFuture[]::new));
    }

    @NotNull
    @Override
    public <T extends BasePlayer, U extends PlayerRestClient<T>> CompletableFuture<T> get(
            @NotNull UUID playerUUID,
            @NotNull Class<U> clientType
    ) {
        return restProvider.get(clientType).getPlayerAsync(playerUUID);
    }

    @Nullable
    @Override
    public <T extends BasePlayer> T getCached(
            @NotNull UUID playerUUID,
            @NotNull Class<T> dataType
    ) {
        return cache.getOrDefault(playerUUID, Collections.emptySet()).stream()
                .filter(data -> data.getClass() == dataType)
                .findFirst()
                .map(dataType::cast)
                .orElse(null);
    }

    @NotNull
    @Override
    public <T extends BasePlayer> CompletableFuture<Void> updateData(
            @NotNull UUID playerUUID,
            @NotNull Class<T> dataClassType,
            @NotNull Consumer<T> dataAction
    ) {
        var restClientTypes = plugin.getRankedServer().requiredPlayerData();
        PlayerRestClient<T> client = restProvider.getByReturnType(dataClassType, restClientTypes);

        T cachedData = getCached(playerUUID, client.getReturnType());
        return client.updatePlayerAsync(playerUUID, cachedData, dataAction);
    }

    @Override
    public void unload(@NotNull UUID playerUUID) {
        cache.remove(playerUUID);
    }

    private void setCachedData(BasePlayer basePlayer) {
        UUID playerId = basePlayer.getId();
        Set<BasePlayer> existingCache = cache.getOrDefault(playerId, new HashSet<>());

        existingCache.removeIf(data -> data.getClass().isAssignableFrom(basePlayer.getClass()));
        existingCache.add(basePlayer);

        cache.put(playerId, existingCache);
    }
}