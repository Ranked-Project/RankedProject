package net.rankedproject.spigot.registrar.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.command.RankedCommand;
import net.rankedproject.spigot.instantiator.impl.CommandManagerInstantiator;
import net.rankedproject.spigot.registrar.ExecutionPriority;
import net.rankedproject.spigot.registrar.Registrar;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CommandRegistrar implements Registrar {

    private static final String PACKAGE_LOOKUP_NAME = "net.rankedproject";

    private final CommonPlugin plugin;

    @Override
    public void register() {
        var injector = plugin.getInjector();
        var commandManager = injector.getInstance(CommandManagerInstantiator.class).get();

        var reflections = new Reflections(PACKAGE_LOOKUP_NAME);
        reflections.getSubTypesOf(RankedCommand.class).forEach(classType -> {
            var rankedCommand = injector.getInstance(classType);
            if (rankedCommand.shouldSkipCommandRegistration(plugin)) {
                return;
            }

            var command = rankedCommand.command(commandManager);
            commandManager.command(command);
        });
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.FIRST;
    }
}
