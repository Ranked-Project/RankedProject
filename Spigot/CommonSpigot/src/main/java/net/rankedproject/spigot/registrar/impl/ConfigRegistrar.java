package net.rankedproject.spigot.registrar.impl;

import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.registrar.AsyncRegistrar;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ConfigRegistrar implements AsyncRegistrar {

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public CompletableFuture<?> registerAsync() {
        var injector = plugin.getInjector();
        var rankedServer = plugin.getRankedServer();

        var configs = rankedServer.configs();
        return ConfigProvider.loadAll(configs, injector);
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.FIRST;
    }
}