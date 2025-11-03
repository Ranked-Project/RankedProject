package net.rankedproject.spigot.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.CommonPlugin;

@RequiredArgsConstructor
public class PluginBinderModule extends AbstractModule {

    private final CommonPlugin plugin;

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(CommonPlugin.class).toInstance(plugin);
    }
}