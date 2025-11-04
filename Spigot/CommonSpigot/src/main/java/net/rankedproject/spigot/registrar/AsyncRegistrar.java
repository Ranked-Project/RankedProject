package net.rankedproject.spigot.registrar;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface AsyncRegistrar extends Registrar {

    @Override
    default void register() {
        throw new UnsupportedOperationException("Called #register() on AsyncRegistrar when should have called #registerAsync() instead.");
    }

    @NotNull
    CompletableFuture<?> registerAsync();
}
