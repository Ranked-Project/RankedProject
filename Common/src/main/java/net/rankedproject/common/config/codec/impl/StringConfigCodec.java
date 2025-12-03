package net.rankedproject.common.config.codec.impl;

import com.google.common.base.Preconditions;
import java.util.List;
import net.rankedproject.common.config.codec.ConfigCodec;
import org.jetbrains.annotations.NotNull;

public class StringConfigCodec implements ConfigCodec<String, Object> {

    @NotNull
    @Override
    public String parse(@NotNull Object serialized) {
        Preconditions.checkArgument(serialized instanceof String, "Couldn't parse %s, the value is not String".formatted(serialized));
        return (String) serialized;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public List<String> parseList(@NotNull Object serialized) {
        Preconditions.checkArgument(serialized instanceof List, "Couldn't parse %s, the value is not String".formatted(serialized));
        return (List<String>) serialized;
    }
}
