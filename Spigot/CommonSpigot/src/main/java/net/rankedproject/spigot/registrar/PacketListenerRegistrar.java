package net.rankedproject.spigot.registrar;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.spigot.CommonPlugin;
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

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.LOADED_DEFAULTS;
    }
}
