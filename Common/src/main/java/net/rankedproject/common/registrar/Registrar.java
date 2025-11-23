package net.rankedproject.common.registrar;

import org.jetbrains.annotations.NotNull;

public interface Registrar {

    /**
     * Registers necessities on plugin startup
     */
    void register();

    @NotNull
    ExecutionPriority getPriority();
}
