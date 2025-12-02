package net.rankedproject.common.registry;

import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.UnmodifiableView;

public abstract class BaseRegistry<K, V> {

    private final Map<K, V> registeredEntries;

    public BaseRegistry(Map<K, V> registeredEntries) {
        this.registeredEntries = registeredEntries;
    }

    public void register(K key, V value) {
        registeredEntries.put(key, value);
    }

    public V get(K key) {
        return registeredEntries.get(key);
    }

    @UnmodifiableView
    public Map<K, V> getAllRegistered() {
        return Collections.unmodifiableMap(registeredEntries);
    }
}
