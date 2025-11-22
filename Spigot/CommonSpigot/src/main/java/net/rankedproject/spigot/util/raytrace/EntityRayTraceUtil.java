package net.rankedproject.spigot.util.raytrace;

import com.github.retrooper.packetevents.util.Vector3d;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class EntityRayTraceUtil {

    private static final float HITBOX_OFFSET = 0.25F;

    public static boolean isLookingAtEntity(
            @NotNull Player player,
            @NotNull Location entityLocation,
            @NotNull EntityType<?> entityType,
            int entitySize
    ) {
        var location = player.getLocation();
        var eyeVector = new Vector3d(location.getX(), location.getY() + player.getEyeHeight(), location.getZ());

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        double interactRange = Objects.requireNonNull(player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)).getBaseValue();
        Vector3d look = RayTraceUtil.getLookVector(yaw, pitch).multiply(interactRange);
        Vector3d target = eyeVector.add(look);

        float hitboxWidth = (entityType.getWidth() - HITBOX_OFFSET) * entitySize;
        float hitboxHeight = entityType.getHeight() * entitySize;
        var area = new Area(
                entityLocation.x() - hitboxWidth, entityLocation.y(), entityLocation.z() - hitboxWidth,
                entityLocation.x() + hitboxWidth, entityLocation.y() + hitboxHeight, entityLocation.z() + hitboxWidth
        );

        Vector3d intercept = RayTraceUtil.calculateIntercept(area, eyeVector, target);
        return intercept != null;
    }
}