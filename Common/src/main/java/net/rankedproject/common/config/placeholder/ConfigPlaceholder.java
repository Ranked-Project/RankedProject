package net.rankedproject.common.config.placeholder;

import org.jetbrains.annotations.NotNull;

public record ConfigPlaceholder(String placeholder, String value) {

    public static ConfigPlaceholder of(@NotNull String placeholder, @NotNull String value) {
        return new ConfigPlaceholder(placeholder, value);
    }

    public static ConfigPlaceholder of(@NotNull String placeholder, @NotNull Object value) {
        return new ConfigPlaceholder(placeholder, value.toString());
    }
}