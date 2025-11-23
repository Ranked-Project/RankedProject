package net.rankedproject.spigot.registrar.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import net.rankedproject.spigot.registrar.Registrar;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PacketListenerRegistrar implements Registrar {

    private static final String PACKAGE_LOOKUP_NAME = "net.rankedproject";

    private final CommonPlugin plugin;

    @Override
    public void register() {
        var reflections = new Reflections(PACKAGE_LOOKUP_NAME);
        var packetListenerClasses = reflections.getSubTypesOf(PacketListener.class);

        var packetEventManager = PacketEvents.getAPI().getEventManager();
        packetListenerClasses.forEach(packetListenerClass -> {
            var packetListener = plugin.getInjector().getInstance(packetListenerClass);
            packetEventManager.registerListener(packetListener, PacketListenerPriority.NORMAL);
        });
    }

    @Override
    public @NotNull ExecutionPriority getPriority() {
        return ExecutionPriority.LOADED_DEFAULTS;
    }
}