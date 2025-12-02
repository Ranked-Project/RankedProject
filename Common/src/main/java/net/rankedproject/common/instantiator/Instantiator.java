package net.rankedproject.common.instantiator;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public interface Instantiator<T> {

    Map<Class<?>, Object> INSTANTIATED_FIELDS = new ConcurrentHashMap<>();

    default void init() {
        var initiatedData = initInternally();
        Preconditions.checkNotNull(initiatedData);

        INSTANTIATED_FIELDS.put(getClass(), initiatedData);
    }

    /**
     * Loads T into memory.
     * <p>
     * This method is typically invoked during server startup or other initialization phases
     * to perform heavy I/O or setup tasks. It should return a fully initialized instance
     * of {@code T}.
     *
     * @return a non-null, fully initialized {@code T} instance ready for use
     */
    @NotNull
    T initInternally();

    /**
     * Retrieves the currently loaded T instance, if available.
     * <p>
     * If {@link #init()} has not been called yet (or failed), this method
     * may return {@code null}.
     *
     * @return the loaded {@code T} instance if available, or {@code null} if not yet loaded
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default T get() {
        T instantiatedField = (T) INSTANTIATED_FIELDS.get(getClass());
        Preconditions.checkNotNull(instantiatedField, "%s never initialized".formatted(getClass()));

        return instantiatedField;
    }
}
