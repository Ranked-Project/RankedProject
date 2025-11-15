package net.rankedproject.spigot.config.codec;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.codec.ConfigCodec;
import net.rankedproject.common.config.type.ConfigSection;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ConfigSectionCodec implements ConfigCodec<ConfigSection, MemorySection> {

    private final Injector injector;

    @NotNull
    @Override
    public ConfigSection parse(@NotNull MemorySection serialized) {
        return ConfigSection.builder()
                .path(serialized.getCurrentPath())
                .injector(injector)
                .build();
    }

    @NotNull
    @Override
    public List<ConfigSection> parseList(@NotNull MemorySection serialized) {
        throw new UnsupportedOperationException("Can't parse a list of MemorySection");
    }
}
