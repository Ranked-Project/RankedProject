package net.rankedproject.common.config.codec;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConfigCodec<T, U> {

    @NotNull
    T parse(@NotNull U serialized);

    @NotNull
    List<T> parseList(@NotNull U serialized);
}
