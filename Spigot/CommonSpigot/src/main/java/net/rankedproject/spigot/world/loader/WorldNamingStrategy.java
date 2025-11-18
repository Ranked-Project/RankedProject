package net.rankedproject.spigot.world.loader;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public enum WorldNamingStrategy {

    /**
     * The world's name will follow the template "worldname_randomuuid".
     * For example, a world named "Equinox" would become
     * "Equinox_c2d3d415-dbdd-4d1a-92a5-33badd11d4be".
     */
    RANDOM_UUID_NAME(name -> "%s_%s".formatted(name, UUID.randomUUID())),

    /**
     * The world's name will remain unchanged.
     * For example, a world named "Equinox" will stay as "Equinox".
     */
    NORMAL_NAME(Function.identity());

    private final Function<String, String> renamingAction;

    @NotNull
    public String renameWorld(@NotNull String worldName) {
        return renamingAction.apply(worldName);
    }
}
