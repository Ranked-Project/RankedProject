package net.rankedproject.spigot.world.util;

import com.google.common.base.Preconditions;
import com.infernalsuite.asp.api.loaders.SlimeLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SlimeResourceLoader implements SlimeLoader {

    private static final String PATH = "worlds/%s.slime";

    @Override
    public byte[] readWorld(String worldName) throws IOException {
        var resourcePath = PATH.formatted(worldName);
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            Preconditions.checkNotNull(inputStream, "Slime world named %s not found in %s".formatted(worldName, resourcePath));
            return inputStream.readAllBytes();
        }
    }

    @Override
    public boolean worldExists(String worldName) throws IOException {
        var resourcePath = PATH.formatted(worldName);
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            return inputStream != null;
        }
    }

    @Override
    public List<String> listWorlds() {
        return Collections.emptyList();
    }

    @Override
    public void saveWorld(String worldName, byte[] serializedWorld) {

    }

    @Override
    public void deleteWorld(String worldName) {

    }
}
