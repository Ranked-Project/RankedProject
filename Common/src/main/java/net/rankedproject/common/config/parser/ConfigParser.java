package net.rankedproject.common.config.parser;

import com.google.common.base.Preconditions;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.codec.ConfigCodec;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class ConfigParser<T> {

    private final Map<Class<?>, ConfigCodec<?, ?>> codecs;

    @NotNull
    @SuppressWarnings("unchecked")
    protected <U, V> ConfigCodec<U, V> getCodecByReturnType(Class<? extends U> returnType) {
        var configCodec = codecs.get(returnType);
        var returnTypeName = returnType.getSimpleName();

        Preconditions.checkNotNull(configCodec, "Couldn't find codec with return type %s".formatted(returnTypeName));
        return (ConfigCodec<U, V>) configCodec;
    }

    @NotNull
    public <R, U> R get(
            @NotNull String path,
            @NotNull ParsedConfig<T> parsedConfig,
            @NotNull Class<R> returnType
    ) {
        U configData = getConfigData(path, parsedConfig);
        var codec = getCodecByReturnType(returnType);
        return codec.parse(configData);
    }

    @NotNull
    public <R, U> List<R> getAsList(
            @NotNull String path,
            @NotNull ParsedConfig<T> parsedConfig,
            @NotNull Class<R> returnType
    ) {
        U configData = getConfigData(path, parsedConfig);
        ConfigCodec<R, U> codec = getCodecByReturnType(returnType);
        return codec.parseList(configData);
    }

    @NotNull
    public Integer getAsInt(@NotNull String path, @NotNull ParsedConfig<T> parsedConfig) {
        return get(path, parsedConfig, Integer.class);
    }

    @NotNull
    public String getAsString(@NotNull String path, @NotNull ParsedConfig<T> parsedConfig) {
        return get(path, parsedConfig, String.class);
    }

    @NotNull
    public abstract ParsedConfig<T> parse(@NotNull Reader reader);

    @NotNull
    protected abstract <R> R getConfigData(
            @NotNull String path,
            @NotNull ParsedConfig<T> parsedConfig
    );
}
