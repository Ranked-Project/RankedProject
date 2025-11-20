package net.rankedproject.game.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import net.rankedproject.game.GamePlugin;
import net.rankedproject.game.finder.GameFinder;

@RequiredArgsConstructor
public class GamePluginModule extends AbstractModule {

    private final GamePlugin plugin;

    @Override
    protected void configure() {
        bind(GamePlugin.class).toInstance(plugin);
        bind(new TypeLiteral<GameFinder<?>>() {}).toProvider(plugin);
    }
}
