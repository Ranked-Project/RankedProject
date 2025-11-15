package net.rankedproject.common.localization;

import com.google.inject.Injector;
import net.rankedproject.common.config.placeholder.ConfigPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record LocalizationReadOption(String path, List<UUID> playerUUIDs, List<ConfigPlaceholder> placeholders) {

    public static Builder builder(@NotNull Injector injector) {
        return new Builder(injector);
    }

    public static class Builder {

        private String path;
        private final Injector injector;

        private List<UUID> playerUUIDs = new ArrayList<>();
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
        public Builder playerUUID(@NotNull UUID playerUUID) {
            this.playerUUIDs.add(playerUUID);
            return this;
        }

        @NotNull
        public Builder placeholder(@NotNull String placeholder, @NotNull String value) {
            this.placeholders.add(new ConfigPlaceholder(placeholder, value));
            return this;
        }

        @NotNull
        public Builder placeholder(@NotNull String placeholder, @NotNull Object value) {
            this.placeholders.add(new ConfigPlaceholder(placeholder, value.toString()));
            return this;
        }

        @NotNull
        public Builder playerUUIDs(@NotNull List<UUID> playerUUIDs) {
            this.playerUUIDs = playerUUIDs;
            return this;
        }

        @NotNull
        public String get(@NotNull String fileName) {
            return injector.getInstance(Localization.class).get(fileName, build());
        }

        public void sendMessage(@NotNull String fileName) {
            injector.getInstance(Localization.class).sendMessage(fileName, build());
        }

        @NotNull
        public LocalizationReadOption build() {
            return new LocalizationReadOption(path, playerUUIDs, placeholders);
        }
    }
}