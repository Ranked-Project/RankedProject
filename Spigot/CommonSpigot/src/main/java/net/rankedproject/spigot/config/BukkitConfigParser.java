package net.rankedproject.spigot.config;

import com.google.common.base.Preconditions;
import com.google.inject.Singleton;
import net.rankedproject.common.config.codec.impl.IntegerConfigCodec;
import net.rankedproject.common.config.codec.impl.StringConfigCodec;
import net.rankedproject.common.config.parser.ConfigParser;
import net.rankedproject.common.config.parser.ParsedConfig;
import net.rankedproject.spigot.config.codec.LocationConfigCodec;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.Map;

@Singleton
public class BukkitConfigParser extends ConfigParser<YamlConfiguration> {

    public BukkitConfigParser() {
        super(Map.of(
                String.class, new StringConfigCodec(),
                Integer.class, new IntegerConfigCodec(),
                Location.class, new LocationConfigCodec()
        ));
    }

    @NotNull
    @Override
    public ParsedConfig<YamlConfiguration> parse(@NotNull Reader reader) {
        return new ParsedConfig<>(YamlConfiguration.loadConfiguration(reader), YamlConfiguration.class);
    }

    @NotNull
    @Override
    public <U> U getConfigData(
            @NotNull String path,
            @NotNull ParsedConfig<YamlConfiguration> parsedConfig
    ) {
        var data = parsedConfig.data();
        var configData = data.get(path);
        Preconditions.checkNotNull(configData, "Couldn't find any config data by path %s".formatted(path));

        return (U) configData;
    }
}