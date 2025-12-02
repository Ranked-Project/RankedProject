package net.rankedproject.common.config.reader;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.config.placeholder.ConfigPlaceholder;
import net.rankedproject.common.config.type.ConfigSection;
import org.jetbrains.annotations.NotNull;

public record ConfigReadOption(String path,
                               Injector injector,
                               Class<? extends Config> configType,
                               List<ConfigPlaceholder> placeholders) {

    public static ConfigReadOption.Builder builder(@NotNull Injector injector) {
        return new ConfigReadOption.Builder(injector);
    }

    public static class Builder {

        private String path;
        private Class<? extends Config> configType;

        private final Injector injector;
        private final List<ConfigPlaceholder> placeholders = new ArrayList<>();

        public Builder(@NotNull Injector injector) {
            this.injector = injector;
        }

        @NotNull
        public Builder path(@NotNull String path) {
            this.path = path;
            return this;
        }

        @NotNull
        public Builder config(@NotNull Class<? extends Config> configType) {
            this.configType = configType;
            return this;
        }

        @NotNull
        public Builder placeholder(@NotNull String placeholder, @NotNull Object value) {
            this.placeholders.add(ConfigPlaceholder.of(placeholder, value));
            return this;
        }

        @NotNull
        public <T> T get(Class<T> returnType) {
            return injector.getInstance(ConfigReader.class).get(returnType, build());
        }

        @NotNull
        public <T> List<T> getAsList(Class<T> returnType) {
            return injector.getInstance(ConfigReader.class).getAsList(returnType, build());
        }

        @NotNull
        public String getAsString() {
            return injector.getInstance(ConfigReader.class).getAsString(build());
        }

        @NotNull
        public Integer getAsInt() {
            return injector.getInstance(ConfigReader.class).getAsInt(build());
        }

        @NotNull
        public ConfigSection getAsSection() {
            return injector.getInstance(ConfigReader.class).getAsSection(build());
        }

        @NotNull
        public ConfigReadOption build() {
            Preconditions.checkNotNull(injector, "You didn't provide Guice Injector");
            return new ConfigReadOption(path, injector, configType, placeholders);
        }
    }
}
