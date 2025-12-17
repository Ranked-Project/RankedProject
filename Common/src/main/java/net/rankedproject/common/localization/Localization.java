package net.rankedproject.common.localization;

import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.placeholder.ConfigPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@RequiredArgsConstructor
public abstract class Localization {

    private final Injector injector;

    static {
        Locale.setDefault(Locale.US);
    }

    public @NotNull LocalizationReadOption.Builder builder(
            final @NotNull String fileName,
            final @NotNull String path,
            final @NotNull UUID playerUUID
    ) {
        return LocalizationReadOption.builder(injector)
                .fileName(fileName)
                .path(path)
                .playerUUID(playerUUID);
    }

    public @NotNull String get(final @NotNull String fileName, final @NotNull LocalizationReadOption readOption) {
        var resourceBundle = getResourceBundle(fileName);
        var string = resourceBundle.getString(readOption.path());

        return applyPlaceholders(string, readOption.placeholders());
    }

    public @NotNull List<String> getList(final @NotNull String fileName, final @NotNull LocalizationReadOption readOption) {
        var resourceBundle = getResourceBundle(fileName);
        var stringArray = resourceBundle.getStringArray(readOption.path());

        return Arrays.stream(stringArray)
                .map(string -> applyPlaceholders(string, readOption.placeholders()))
                .toList();
    }

    protected @NotNull String applyPlaceholders(
            final @NotNull String string,
            final @NotNull List<ConfigPlaceholder> placeholders
    ) {
        var result = string;
        for (var placeholder : placeholders) {
            var placeholderName = placeholder.placeholder();
            if (placeholderName.charAt(0) != '%') {
                placeholderName = "%" + placeholderName + "%";
            }

            result = result.replace(placeholderName, placeholder.value());
        }
        return result;
    }

    protected @NotNull ResourceBundle getResourceBundle(final @NotNull String fileName) {
        return findResourceBundle(fileName, Locale.getDefault());
    }

    protected @NotNull ResourceBundle findResourceBundle(final @NotNull String fileName, final @NotNull Locale locale) {
        return ResourceBundle.getBundle(fileName, locale);
    }

    public abstract void sendMessage(final @NotNull String fileName, final @NotNull LocalizationReadOption readOption);
}
