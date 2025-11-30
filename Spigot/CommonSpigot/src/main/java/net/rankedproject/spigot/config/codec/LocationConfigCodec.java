package net.rankedproject.spigot.config.codec;

import com.google.common.base.Preconditions;
import net.rankedproject.common.config.codec.ConfigCodec;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationConfigCodec implements ConfigCodec<Location, Object> {

    @NotNull
    @Override
    public Location parse(@NotNull Object serialized) {
        Preconditions.checkArgument(serialized instanceof MemorySection, "Couldn't parse %s, the value is not MemorySection".formatted(serialized));
        MemorySection section = (MemorySection) serialized;

        World world = null;
        var worldName = section.getString("world-name");
        if (worldName != null) {
            world = Bukkit.getWorld(worldName);
        }

        var x = section.getDouble("x");
        var y = section.getDouble("y");
        var z = section.getDouble("z");
        var yaw = (float) section.getDouble("yaw", 0);
        var pitch = (float) section.getDouble("pitch", 0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    @NotNull
    @Override
    public List<Location> parseList(@NotNull Object serialized) {
        Preconditions.checkArgument(serialized instanceof List<?>, "Couldn't parse %s, the value is not a List".formatted(serialized));
        List<Map<String, Object>> sections = (List<Map<String, Object>>) serialized;

        List<Location> locations = new ArrayList<>();
        for (Map<String, Object> map : sections) {
            World world = null;
            var worldName = (String) map.get("world-name");
            if (worldName != null) {
                world = Bukkit.getWorld(worldName);
            }

            double x = (Double) map.get("x");
            double y = (Double) map.get("y");
            double z = (Double) map.get("z");
            float yaw = map.containsKey("yaw") ? ((Number) map.get("yaw")).floatValue() : 0;
            float pitch = map.containsKey("pitch") ? ((Number) map.get("pitch")).floatValue() : 0;
            locations.add(new Location(world, x, y, z, yaw, pitch));
        }

        return locations;
    }
}
