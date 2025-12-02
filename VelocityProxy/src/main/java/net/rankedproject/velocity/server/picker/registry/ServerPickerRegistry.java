package net.rankedproject.velocity.server.picker.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rankedproject.common.network.server.picker.ServerPickerType;
import net.rankedproject.common.registry.BaseRegistry;
import net.rankedproject.velocity.server.picker.ServerPicker;

import java.util.HashMap;

@Singleton
public class ServerPickerRegistry extends BaseRegistry<ServerPickerType, ServerPicker> {

    @Inject
    public ServerPickerRegistry() {
        super(new HashMap<>());
    }
}