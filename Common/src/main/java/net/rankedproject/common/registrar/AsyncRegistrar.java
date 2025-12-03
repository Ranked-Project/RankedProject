package net.rankedproject.common.registrar;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public interface AsyncRegistrar extends Registrar {

    @Override
    default void register() {
        throw new UnsupportedOperationException("Called #register() on AsyncRegistrar when should have called #registerAsync() instead.");
    }

    @NotNull
    CompletableFuture<?> registerAsync();
}
