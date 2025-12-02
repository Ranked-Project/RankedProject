package net.rankedproject.common.config;

import com.google.inject.Injector;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.rankedproject.common.config.accessor.ConfigAccessor;
import net.rankedproject.common.config.reader.ConfigReadOption;
import org.jetbrains.annotations.NotNull;

public class ConfigProvider {

    @NotNull
    public static <T extends Config> ConfigReadOption.Builder get(
            @NotNull Class<T> configType,
            @NotNull Injector injector
    ) {
        return ConfigReadOption.builder(injector).config(configType);
    }

    @NotNull
    public static CompletableFuture<?> load(
            @NotNull Class<? extends Config> configType,
            @NotNull Injector injector
    ) {
        var config = injector.getInstance(configType);
        var configAccessor = injector.getInstance(ConfigAccessor.class);

        return configAccessor.loadAsync(config);
    }

    @NotNull
    public static CompletableFuture<?> loadAll(
            @NotNull Collection<Class<? extends Config>> types,
            @NotNull Injector injector
    ) {
        var configAccessor = injector.getInstance(ConfigAccessor.class);
        return configAccessor.loadAll(types, injector);
    }
}
