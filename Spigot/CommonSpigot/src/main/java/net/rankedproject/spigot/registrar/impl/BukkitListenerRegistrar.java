package net.rankedproject.spigot.registrar.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import net.rankedproject.spigot.registrar.Registrar;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class BukkitListenerRegistrar implements Registrar {

    private static final String PACKAGE_LOOKUP_NAME = "net.rankedproject";

    private final CommonPlugin plugin;

    @Override
    public void register() {
        var injector = plugin.getInjector();
        var pluginManager = Bukkit.getPluginManager();

        var reflections = new Reflections(PACKAGE_LOOKUP_NAME);
        reflections.getSubTypesOf(Listener.class).forEach(classType -> {
            if (EventExecutor.class.isAssignableFrom(classType)) {
                return;
            }

            var listener = injector.getInstance(classType);
            pluginManager.registerEvents(listener, plugin);
        });
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.FIRST;
    }
}