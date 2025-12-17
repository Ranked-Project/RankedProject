package net.rankedproject.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class CacheWrapper<T> {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    // Some weak key based cache which is going to remove the entry after 5 minutes if the object hasn't been used for all that time
    private static final Map<Object, CacheWrapper<?>> cachedObjectsMap = new HashMap<>();

    private final WeakReference<T> storedObject;

    private CacheWrapper(final @NotNull T object, final @NotNull Consumer<T> cacheAction) {
        this.storedObject = new WeakReference<>(object);

        cacheAction.accept(object);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> cacheAction.accept(object), 0, 1, TimeUnit.SECONDS);
    }

    public @Nullable T getStoredObject() {
        return storedObject.get();
    }

    @SuppressWarnings("unchecked")
    public static <T> CacheWrapper<T> of(final @NotNull T object, final @NotNull Consumer<T> cacheAction) {
        return (CacheWrapper<T>) cachedObjectsMap.computeIfAbsent(object, _ -> new CacheWrapper<>(object, cacheAction));
    }
}
