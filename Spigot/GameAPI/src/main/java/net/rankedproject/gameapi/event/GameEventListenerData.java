package net.rankedproject.gameapi.event;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record GameEventListenerData<E>(String id, Class<E> eventType, Consumer<E> eventAction, List<Predicate<E>> filters) {

    public static <E extends Event> GameEventListenerData.Builder<E> builder() {
        return new GameEventListenerData.Builder<>();
    }

    public final static class Builder<E extends Event> {

        private String id = UUID.randomUUID().toString();

        private Class<E> eventType;
        private Consumer<E> eventAction;

        private List<Predicate<E>> filters = Collections.emptyList();

        public GameEventListenerData.Builder<E> create(Class<E> eventType) {
            this.eventType = eventType;
            return this;
        }

        public GameEventListenerData.Builder<E> create(String id, Class<E> eventType) {
            this.id = id;
            this.eventType = eventType;
            return this;
        }

        public GameEventListenerData.Builder<E> on(Consumer<E> eventAction) {
            this.eventAction = eventAction;
            return this;
        }

        public GameEventListenerData.Builder<E> filter(Predicate<E> filter) {
            if (this.filters.isEmpty()) {
                this.filters = new ArrayList<>();
            }

            this.filters.add(filter);
            return this;
        }

        public GameEventListenerData<E> build() {
            return new GameEventListenerData<>(id, eventType, eventAction, filters);
        }
    }
}
