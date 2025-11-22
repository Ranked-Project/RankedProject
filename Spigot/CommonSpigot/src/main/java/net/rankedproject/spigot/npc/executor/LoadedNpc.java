package net.rankedproject.spigot.npc.executor;

import net.rankedproject.spigot.npc.Npc;

public record LoadedNpc<T extends Npc>(
        int entityId,
        T npc
) {

    public static <T extends Npc> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends Npc> {

        private int entityId;
        private T npc;

        public Builder<T> entityId(int entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder<T> npc(T npc) {
            this.npc = npc;
            return this;
        }

        public LoadedNpc<T> build() {
            return new LoadedNpc<>(entityId, npc);
        }
    }
}