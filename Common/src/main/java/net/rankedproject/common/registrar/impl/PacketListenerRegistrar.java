package net.rankedproject.common.registrar.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import net.rankedproject.common.packet.PacketListener;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PacketListenerRegistrar implements Registrar {

    private static final String PACKAGE_LOOKUP_NAME = "net.rankedproject";

    private final Injector injector;

    @Override
    public void register() {
        var nats = injector.getInstance(NatsInstantiator.class).get();
        var reflection = new Reflections(PACKAGE_LOOKUP_NAME);

        reflection.getSubTypesOf(PacketListener.class).forEach(listener -> {
            var packetListener = injector.getInstance(listener);
            var subject = packetListener.getSubject();
            var dispatcher = nats.createDispatcher(packetListener::onPacket);

            dispatcher.subscribe(subject);
        });
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.FIRST;
    }
}
