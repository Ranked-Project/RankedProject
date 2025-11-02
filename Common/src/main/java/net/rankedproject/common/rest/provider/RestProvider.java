package net.rankedproject.common.rest.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.rest.RestClient;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RestProvider {

    private final Injector injector;

    /**
     * Retrieves an instance of the specified {@code RestClient} type from the registry.
     *
     * @param <V>       The value type of the {@code RestClient}.
     * @param <T>       The specific {@code RestClient} implementation.
     * @param classType The {@code RestClient} class type to retrieve.
     * @return An instance of the specified {@code RestClient} type, or {@code null} if not found.
     * @throws ClassCastException if the retrieved instance cannot be cast to the specified type.
     */
    public <V, T extends RestClient<? extends V>> T get(Class<? extends T> classType) {
        return injector.getInstance(classType);
    }

    /**
     * Retrieves an instance of the specified {@code RestClient} type from the registry based on the return type.
     *
     * <p>This method looks up a registered {@code RestClient} whose return type matches the given {@code classType}.
     * If found, it returns an instance of the specified {@code RestClient}; otherwise, it returns {@code null}.
     *
     * @param <V>       The value type handled by the {@code RestClient}, typically a subtype of {@code BasePlayer}.
     * @param <T>       The specific implementation of {@code RestClient} to be retrieved.
     * @param classType The class type of the value associated with the {@code RestClient}.
     * @return An instance of the matching {@code RestClient} if found, otherwise {@code null}.
     * @throws ClassCastException if the retrieved instance cannot be cast to the specified {@code RestClient} type.
     * @implNote The method performs an unchecked cast to {@code T}, which is suppressed via {@code @SuppressWarnings("unchecked")}.
     */
    @SuppressWarnings("unchecked")
    public <V, T extends RestClient<V>> T getByReturnType(
            @NotNull Class<? extends V> classType,
            @NotNull Collection<? extends Class<? extends RestClient<?>>> restClientTypes
    ) {
        RestClient<?> restClient = restClientTypes
                .stream()
                .map(injector::getInstance)
                .filter(client -> client.getReturnType() == classType)
                .findFirst()
                .orElse(null);

        return restClient == null ? null : (T) restClient;
    }
}
