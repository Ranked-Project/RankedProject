package net.rankedproject.spigot.registrar;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.registrar.ExecutionPriority;
import net.rankedproject.common.registrar.Registrar;
import net.rankedproject.spigot.npc.Npc;
import net.rankedproject.spigot.npc.registry.AutoSpawnNpcRegistry;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AutoSpawnNpcRegistrar implements Registrar {

    private static final String PACKAGE_LOOKUP_NAME = "net.rankedproject";

    private final Injector injector;
    private final AutoSpawnNpcRegistry autoSpawnNpcRegistry;

    @Override
    public void register() {
        var reflections = new Reflections(PACKAGE_LOOKUP_NAME);
        Set<Class<? extends Npc>> npcClasses = reflections.getSubTypesOf(Npc.class);

        npcClasses.stream()
                .filter(npcClass -> !Modifier.isAbstract(npcClass.getModifiers()))
                .map(injector::getInstance)
                .filter(npc -> npc.getBehavior().autoSpawn())
                .forEach(npc -> autoSpawnNpcRegistry.register(npc.getClass(), npc));
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.LAST;
    }
}
