package net.rankedproject.velocity.server.picker.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.EnumMap;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.common.registry.BaseRegistry;
import net.rankedproject.velocity.server.picker.ServerPicker;

@Singleton
public final class ServerPickerRegistry extends BaseRegistry<ServerPickerType, ServerPicker> {

    /**
     * Creates a registry backed by an {@link EnumMap}, preconfigured
     * to use {@link ServerPickerType} as keys.
     */
    @Inject
    public ServerPickerRegistry() {
        super(new EnumMap<>(ServerPickerType.class));
    }
}
