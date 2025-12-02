package net.rankedproject.common.config.accessor;

import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.config.accessor.impl.CachedConfigAccessor;
import net.rankedproject.common.config.parser.ParsedConfig;
import net.rankedproject.common.config.reader.ConfigReadOption;
import net.rankedproject.common.rest.RestClient;
import org.jetbrains.annotations.NotNull;

@ImplementedBy(CachedConfigAccessor.class)
public interface ConfigAccessor {

    @NotNull
    <T> ParsedConfig<T> get(@NotNull ConfigReadOption readOption);

    @NotNull
    <T extends Config> ParsedConfig<?> load(@NotNull T config);

    @NotNull
    default CompletableFuture<Void> loadAll(
            @NotNull Collection<Class<? extends Config>> types,
            @NotNull Injector injector
    ) {
        var configs = types.stream()
                .map(injector::getInstance)
                .toList();

        return CompletableFuture.allOf(configs.stream()
                .map(this::loadAsync)
                .toArray(CompletableFuture[]::new));
    }

    @NotNull
    default <T extends Config> CompletableFuture<ParsedConfig<?>> loadAsync(@NotNull T config) {
        return CompletableFuture.supplyAsync(() -> load(config), RestClient.EXECUTOR_SERVICE);
    }
}
