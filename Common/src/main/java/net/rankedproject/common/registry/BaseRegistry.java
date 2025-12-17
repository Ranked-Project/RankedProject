package net.rankedproject.common.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;

public abstract class BaseRegistry<K, V> {

    private final Map<K, V> registeredEntries;

    protected BaseRegistry(final @NotNull Map<K, V> registeredEntries) {
        this.registeredEntries = registeredEntries;
    }

    public void register(final @NotNull K key, final @NotNull V value) {
        registeredEntries.put(key, value);
    }

    public V get(final @NotNull K key) {
        return registeredEntries.get(key);
    }

    @UnmodifiableView
    public @NotNull Map<K, V> getAllRegistered() {
        return Collections.unmodifiableMap(registeredEntries);
    }
}
