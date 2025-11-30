package net.rankedproject.spigot.registrar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.registrar.AsyncRegistrar;
import net.rankedproject.common.registrar.ExecutionPriority;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServerProxyRegistrar implements AsyncRegistrar {

    @NotNull
    @Override
    public CompletableFuture<?> registerAsync() {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    @Override
    public ExecutionPriority getPriority() {
        return ExecutionPriority.LAST;
    }
}
