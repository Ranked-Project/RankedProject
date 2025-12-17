package net.rankedproject.common.rest;

import com.google.gson.JsonElement;
import net.rankedproject.common.rest.request.type.RequestContent;
import okhttp3.MediaType;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public interface RestCrudAPI<V> {

    MediaType JSON = MediaType.get("application/json");
    ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * @return REST Repository URL
     */
    @NotNull String getRepository();

    /**
     * @return Expected DTO Class
     */
    @NotNull Class<V> getReturnType();

    @Nullable JsonElement get(final @NotNull Request request);
    void update(final @NotNull RequestContent request);
    void save(final @NotNull RequestContent request);
    void delete(final @NotNull RequestContent request);

    default @NotNull CompletableFuture<JsonElement> getAsJsonAsync(final @NotNull Request request) {
        return async(() -> get(request));
    }

    default @NotNull CompletableFuture<Void> updateSave(final @NotNull RequestContent requestContent) {
        return async(() -> update(requestContent));
    }

    default @NotNull CompletableFuture<Void> saveAsync(final @NotNull RequestContent requestContent) {
        return async(() -> save(requestContent));
    }

    default @NotNull CompletableFuture<Void> deleteAsync(final @NotNull RequestContent requestContent) {
        return async(() -> delete(requestContent));
    }

    default @NotNull CompletableFuture<Void> async(final @NotNull Runnable action) {
        return CompletableFuture.runAsync(action, EXECUTOR_SERVICE);
    }

    default <T> @NotNull CompletableFuture<T> async(final @NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR_SERVICE);
    }

    default void shutdown() {
        EXECUTOR_SERVICE.shutdown();
    }
}
