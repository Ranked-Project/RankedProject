package net.rankedproject.velocity.registrar;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.Subscribe;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.velocity.VelocityProxy;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class VelocityListenerRegistrar implements Registrar {

    private static final String LOOKUP_PACKAGES = "net.rankedproject.velocity";

    private final VelocityProxy plugin;
    private final Injector injector;

    @Override
    public void register() {
        var reflections = new Reflections(LOOKUP_PACKAGES, Scanners.MethodsAnnotated);
        var methods = reflections.getMethodsAnnotatedWith(Subscribe.class);

        methods.stream()
                .map(Method::getDeclaringClass)
                .filter(listenerClass -> !VelocityProxy.class.isAssignableFrom(listenerClass))
                .distinct()
                .forEach(listenerClass -> {
                    var listener = this.injector.getInstance(listenerClass);
                    this.plugin.getProxyServer().getEventManager().register(this.plugin, listener);
                });
    }

    @Override
    public @NotNull ExecutionPriority getPriority() {
        return ExecutionPriority.FIRST;
    }
}
