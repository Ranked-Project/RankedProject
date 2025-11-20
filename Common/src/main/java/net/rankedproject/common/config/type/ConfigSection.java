package net.rankedproject.common.config.type;

import com.google.inject.Injector;
import lombok.Builder;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.config.reader.ConfigReadOption;
import org.jetbrains.annotations.NotNull;

@Builder(toBuilder = true)
public record ConfigSection(Class<? extends Config> configType, String path, Injector injector) {

    @NotNull
    public ConfigReadOption.Builder path(@NotNull String path) {
        return ConfigReadOption.builder(injector)
                .config(configType)
                .path(this.path + "." + path);
    }
}
