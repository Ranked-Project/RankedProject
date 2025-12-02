package net.rankedproject.spigot.util;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@UtilityClass
public class ComponentUtil {

    private final LoadingCache<@NotNull String, Component> CACHE = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .maximumSize(10_000)
            .build(message -> MiniMessage.miniMessage().deserialize(message));

    public Component deserialize(String message) {
        return CACHE.get(message);
    }
}
