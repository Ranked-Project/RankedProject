package net.rankedproject.common.localization;

import com.google.common.base.Preconditions;
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
        private String fileName;
        private final Injector injector;

        private List<UUID> playerUUIDs = new ArrayList<>();
        private final List<ConfigPlaceholder> placeholders = new ArrayList<>();

        private Builder(final @NotNull Injector injector) {
            this.injector = injector;
        }

        public @NotNull Builder path(final @NotNull String path) {
            this.path = path;
            return this;
        }

        public @NotNull Builder fileName(final @NotNull String fileName) {
            this.fileName = fileName;
            return this;
        }

        public @NotNull Builder playerUUID(final @NotNull UUID playerUUID) {
            this.playerUUIDs.add(playerUUID);
            return this;
        }

        public @NotNull Builder placeholder(final @NotNull String placeholder, final @NotNull Object value) {
            this.placeholders.add(ConfigPlaceholder.of(placeholder, value));
            return this;
        }

        public @NotNull Builder playerUUIDs(final @NotNull List<UUID> playerUUIDs) {
            this.playerUUIDs = playerUUIDs;
            return this;
        }

        public @NotNull String get() {
            return injector.getInstance(Localization.class).get(fileName, build());
        }

        public void sendMessage() {
            injector.getInstance(Localization.class).sendMessage(fileName, build());
        }

        public @NotNull LocalizationReadOption build() {
            Preconditions.checkNotNull(injector, "You didn't provide Guice Injector");
            return new LocalizationReadOption(path, playerUUIDs, placeholders);
        }
    }
}
