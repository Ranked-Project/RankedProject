package net.rankedproject.spigot.registrar.impl;

import net.rankedproject.spigot.registrar.AsyncRegistrar;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ServerProxyRegistrar implements AsyncRegistrar {

    @NotNull
    @Override
    public CompletableFuture<?> registerAsync() {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.LAST;
    }
}