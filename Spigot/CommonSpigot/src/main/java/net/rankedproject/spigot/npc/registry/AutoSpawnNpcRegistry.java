package net.rankedproject.spigot.npc.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rankedproject.common.registry.BaseRegistry;
import net.rankedproject.spigot.npc.Npc;

import java.util.HashMap;

@Singleton
public class AutoSpawnNpcRegistry extends BaseRegistry<Class<? extends Npc>, Npc> {

    @Inject
    public AutoSpawnNpcRegistry() {
        super(new HashMap<>());
    }
}
