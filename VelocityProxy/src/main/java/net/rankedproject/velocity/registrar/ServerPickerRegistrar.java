package net.rankedproject.velocity.registrar;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.velocity.server.picker.ServerPicker;
import net.rankedproject.velocity.server.picker.registry.ServerPickerRegistry;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerPickerRegistrar implements Registrar {

    private static final String LOOKUP_PACKAGE = "net.rankedproject.velocity";

    private final ServerPickerRegistry serverPickerRegistry;
    private final Injector injector;

    @Override
    public void register() {
        var reflections = new Reflections(LOOKUP_PACKAGE);
        var serverPickerClasses = reflections.getSubTypesOf(ServerPicker.class);

        serverPickerClasses.forEach(serverPickerClass -> {
            var serverPicker = injector.getInstance(serverPickerClass);
            var serverPickerType = serverPicker.getServerPickerType();

            serverPickerRegistry.register(serverPickerType, serverPicker);
        });
    }

    @Override
    public @NotNull ExecutionPriority getPriority() {
        return ExecutionPriority.LOADED_DEFAULTS;
    }
}