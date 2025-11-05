package net.rankedproject.spigot.instantiator.impl;

import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.loaders.mongo.MongoLoader;
import lombok.SneakyThrows;
import net.rankedproject.common.util.EnvironmentUtil;
import net.rankedproject.spigot.instantiator.Instantiator;
import net.rankedproject.spigot.world.util.SlimeResourceLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SlimeLoaderInstantiator implements Instantiator<SlimeLoader> {

    private static final String MONGO_DATABASE = Optional
            .ofNullable(System.getenv("MONGO_DATABASE"))
            .orElseThrow();

    private static final String MONGO_COLLECTION = Optional
            .ofNullable(System.getenv("MONGO_COLLECTION"))
            .orElseThrow();

    private static final String MONGO_USERNAME = System.getenv("MONGO_USERNAME");

    private static final String MONGO_PASSWORD = System.getenv("MONGO_PASSWORD");

    private static final String MONGO_AUTH_SOURCE = System.getenv("MONGO_AUTH_SOURCE");

    private static final String MONGO_HOST = Optional
            .ofNullable(System.getenv("MONGO_HOST"))
            .orElse("127.0.0.1");

    private static final Integer MONGO_PORT = Optional
            .ofNullable(System.getenv("MONGO_PORT"))
            .map(Integer::parseInt)
            .orElse(27017);

    private static final String MONGO_URI = System.getenv("MONGO_URI");

    private SlimeLoader slimeLoader;

    @NotNull
    @Override
    @SneakyThrows
    public SlimeLoader init() {
        if (EnvironmentUtil.isTesting()) {
            slimeLoader = new SlimeResourceLoader();
            return slimeLoader;
        }

        slimeLoader = new MongoLoader(MONGO_DATABASE, MONGO_COLLECTION, MONGO_USERNAME, MONGO_PASSWORD, MONGO_AUTH_SOURCE, MONGO_HOST, MONGO_PORT, MONGO_URI);
        return slimeLoader;
    }

    @NotNull
    @Override
    public SlimeLoader get() {
        return slimeLoader;
    }
}