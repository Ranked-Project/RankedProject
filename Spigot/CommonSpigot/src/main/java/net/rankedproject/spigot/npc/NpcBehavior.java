package net.rankedproject.spigot.npc;

import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.click.NpcClickBehavior;
import net.rankedproject.spigot.npc.model.NpcModel;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record NpcBehavior(
        Location location,
        NpcModel model,
        NpcClickBehavior clickBehavior,
        EntityType<?> entityType,
        int entitySize
) {

    public static NpcBehavior.Builder builder() {
        return new NpcBehavior.Builder();
    }

    public static class Builder {

        private Location location;

        private NpcModel model;
        private NpcClickBehavior clickBehavior;
        private EntityType<?> entityType;

        private int entitySize = 1;

        @NotNull
        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        @NotNull
        public Builder entityType(EntityType<?> entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder model(NpcModel model) {
            this.model = model;
            return this;
        }

        public Builder clickBehavior(NpcClickBehavior clickBehavior) {
            this.clickBehavior = clickBehavior;
            return this;
        }

        public Builder entitySize(int entitySize) {
            this.entitySize = entitySize;
            return this;
        }

        public NpcBehavior build() {
            return new NpcBehavior(location, model, clickBehavior, entityType, entitySize);
        }
    }
}