package net.rankedproject.spigot.npc;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.EntityType;
import net.rankedproject.spigot.npc.click.NpcClickBehavior;
import net.rankedproject.spigot.npc.model.NpcModel;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record NpcBehavior(
        Location location,
        EntityType<?> entityType,
        NpcModel model,
        NpcClickBehavior clickBehavior,
        int entitySize,
        boolean autoSpawn
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
        private boolean autoSpawn = false;

        public Builder location(@NotNull Location location) {
            this.location = location;
            return this;
        }

        public Builder entityType(@NotNull EntityType<?> entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder model(@NotNull NpcModel model) {
            this.model = model;
            return this;
        }

        public Builder clickBehavior(@NotNull NpcClickBehavior clickBehavior) {
            this.clickBehavior = clickBehavior;
            return this;
        }

        public Builder entitySize(int entitySize) {
            this.entitySize = entitySize;
            return this;
        }

        public Builder autoSpawn() {
            this.autoSpawn = true;
            return this;
        }

        public NpcBehavior build() {
            Preconditions.checkNotNull(location, "Location must not be null");
            Preconditions.checkNotNull(entityType, "Entity type must not be null");
            return new NpcBehavior(location, entityType, model, clickBehavior, entitySize, autoSpawn);
        }
    }
}