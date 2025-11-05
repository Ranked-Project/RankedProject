package net.rankedproject.commonspigot.config.type;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.config.ConfigMetadata;
import net.rankedproject.spigot.config.BukkitConfigLoader;
import net.rankedproject.spigot.config.BukkitConfigParser;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigBukkitTest implements Config {

    private final Injector injector;;

    @Inject
    public ConfigBukkitTest(Injector injector) {
        this.injector = injector;
    }

    @Override
    public @NotNull ConfigMetadata getMetadata() {
        return ConfigMetadata.builder()
                .name("config-test.yml")
                .loader(injector.getInstance(BukkitConfigLoader.class))
                .parser(injector.getInstance(BukkitConfigParser.class))
                .build();
    }
}