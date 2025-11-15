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

    @NotNull
    public LocalizationReadOption.Builder path(@NotNull String path, @NotNull UUID playerUUID) {
        return LocalizationReadOption.builder(injector)
                .path(path)
                .playerUUID(playerUUID);
    }

    @NotNull
    public String get(@NotNull String fileName, @NotNull LocalizationReadOption readOption) {
        var resourceBundle = getResourceBundle(readOption.playerUUIDs().getFirst(), fileName);
        var string = resourceBundle.getString(readOption.path());

        return applyPlaceholders(string, readOption.placeholders());
    }

    @NotNull
    public List<String> getList(@NotNull String fileName, @NotNull LocalizationReadOption readOption) {
        var resourceBundle = getResourceBundle(readOption.playerUUIDs().getFirst(), fileName);
        var stringArray = resourceBundle.getStringArray(readOption.path());

        return Arrays.stream(stringArray)
                .map(string -> applyPlaceholders(string, readOption.placeholders()))
                .toList();
    }

    @NotNull
    protected String applyPlaceholders(@NotNull String string, @NotNull List<ConfigPlaceholder> placeholders) {
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

    protected ResourceBundle getResourceBundle(@NotNull UUID playerUUID, @NotNull String fileName) {
        return findResourceBundle(fileName, Locale.getDefault());
    }

    protected ResourceBundle findResourceBundle(@NotNull String fileName, @NotNull Locale locale) {
        return ResourceBundle.getBundle(fileName, locale);
    }

    public abstract void sendMessage(@NotNull String fileName, @NotNull LocalizationReadOption readOption);
}
