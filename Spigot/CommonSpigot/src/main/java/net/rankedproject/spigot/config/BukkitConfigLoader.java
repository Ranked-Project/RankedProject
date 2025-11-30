package net.rankedproject.spigot.config;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.loader.ConfigLoader;
import net.rankedproject.spigot.CommonPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Singleton
@RequiredArgsConstructor(onConstructor_={@Inject})
public class BukkitConfigLoader implements ConfigLoader {

    private final CommonPlugin plugin;

    @NotNull
    @Override
    public Reader load(@NotNull String name) {
        try (var resource = plugin.getClass().getClassLoader().getResourceAsStream(name)) {
            Preconditions.checkNotNull(resource, "Requested config file named %s is not found".formatted(name));

            var bytesArray = resource.readAllBytes();
            var byteStream = new ByteArrayInputStream(bytesArray);

            return new BufferedReader(new InputStreamReader(byteStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
