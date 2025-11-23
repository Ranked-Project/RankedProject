package net.rankedproject.spigot.instantiator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.Instantiator;
import net.rankedproject.spigot.CommonPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CommandManagerInstantiator implements Instantiator<PaperCommandManager<Source>> {

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public PaperCommandManager<Source> initInternally() {
        return PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(plugin);
    }
}
