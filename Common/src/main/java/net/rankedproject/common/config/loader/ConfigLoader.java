package net.rankedproject.common.config.loader;

import java.io.Reader;
import org.jetbrains.annotations.NotNull;

public interface ConfigLoader {

    @NotNull
    Reader load(@NotNull String name);
}
