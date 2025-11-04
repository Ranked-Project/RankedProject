package net.rankedproject.common.config.loader;

import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public interface ConfigLoader {

    @NotNull
    Reader load(@NotNull String name);
}