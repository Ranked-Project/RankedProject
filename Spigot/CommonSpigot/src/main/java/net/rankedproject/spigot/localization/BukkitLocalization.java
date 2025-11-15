package net.rankedproject.spigot.localization;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import net.rankedproject.common.config.placeholder.ConfigPlaceholder;
import net.rankedproject.common.localization.Localization;
import net.rankedproject.common.localization.LocalizationReadOption;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.util.ComponentUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class BukkitLocalization extends Localization {


    @Inject
    public BukkitLocalization(Injector injector) {
        super(injector);
    }

    @Override
    public void sendMessage(@NotNull String fileName, @NotNull LocalizationReadOption readOption) {
        readOption.playerUUIDs().forEach(playerUUID -> {
            var player = Bukkit.getPlayer(playerUUID);
            if (player == null) {
                return;
            }

            var resourceBundle = getResourceBundle(playerUUID, fileName);
            var fetchedLine = resourceBundle.getObject(readOption.path());
            var placeholders = readOption.placeholders();

            if (fetchedLine instanceof String message) {
                sendMessageToPlayer(placeholders, message, player);
            }

            if(fetchedLine instanceof List<?> strings) {
                for (var message : strings) {
                    sendMessageToPlayer(placeholders, (String) message, player);
                }
            }
        });
    }

    @Override
    protected ResourceBundle findResourceBundle(@NotNull String fileName, @NotNull Locale locale) {
        return ResourceBundle.getBundle(fileName, locale, UTF8ResourceBundleControl.utf8ResourceBundleControl());
    }

    private void sendMessageToPlayer(
            @NotNull List<ConfigPlaceholder> placeholders,
            @NotNull String message,
            @NotNull Player player
    ) {
        var messageWithPlaceholders = applyPlaceholders(message, placeholders);
        var componentMessage = ComponentUtil.deserialize(messageWithPlaceholders);

        player.sendMessage(componentMessage);
    }
}
