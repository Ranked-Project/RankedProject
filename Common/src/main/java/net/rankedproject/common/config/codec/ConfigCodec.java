package net.rankedproject.common.config.codec;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ConfigCodec<T, U> {

    @NotNull
    T parse(@NotNull U serialized);

    @NotNull
    List<T> parseList(@NotNull U serialized);
}
