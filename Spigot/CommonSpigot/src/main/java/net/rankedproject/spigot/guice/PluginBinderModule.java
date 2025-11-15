package net.rankedproject.spigot.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.localization.Localization;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.localization.BukkitLocalization;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PluginBinderModule extends AbstractModule {

    private final CommonPlugin plugin;

    public Injector createInjector() {
        var modules = new ArrayList<>(plugin.getRankedServer().modules());
        modules.add(this);

        return Guice.createInjector(modules);
    }

    @Override
    protected void configure() {
        bind(CommonPlugin.class).toInstance(plugin);
        bind(Localization.class).to(BukkitLocalization.class);
    }
}