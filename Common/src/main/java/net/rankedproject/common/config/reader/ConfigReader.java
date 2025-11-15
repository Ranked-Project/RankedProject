package net.rankedproject.common.config.reader;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.config.Config;
import net.rankedproject.common.config.ConfigMetadata;
import net.rankedproject.common.config.accessor.ConfigAccessor;
import net.rankedproject.common.config.parser.ConfigParser;
import net.rankedproject.common.config.parser.ParsedConfig;
import net.rankedproject.common.config.type.ConfigSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ConfigReader {

    private final ConfigAccessor configAccessor;
    private final Injector injector;

    @NotNull
    @SuppressWarnings("unchecked")
    public <R, U> R get(@NotNull Class<R> returnType, @NotNull ConfigReadOption readOption) {
        var config = injector.getInstance(readOption.configType());

        ParsedConfig<U> parsedConfig = configAccessor.get(readOption);
        var metadata = config.getMetadata();

        ConfigParser<U> parser = (ConfigParser<U>) metadata.parser();
        var line = parser.get(readOption.path(), parsedConfig, returnType);

        if (returnType == String.class) {
            line = (R) applyPlaceholder((String) line, readOption);
        }

        return line;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <R, U> List<R> getAsList(@NotNull Class<R> returnType, @NotNull ConfigReadOption readOption) {
        Config config = injector.getInstance(readOption.configType());

        ParsedConfig<U> parsedConfig = configAccessor.get(readOption);
        ConfigMetadata metadata = config.getMetadata();

        ConfigParser<U> parser = (ConfigParser<U>) metadata.parser();
        List<R> list = parser.getAsList(readOption.path(), parsedConfig, returnType);

        if (returnType == String.class) {
            list = (List<R>) list.stream()
                    .map(line -> applyPlaceholder((String) line, readOption))
                    .toList();
        }

        return list;
    }

    @NotNull
    public Integer getAsInt(@NotNull ConfigReadOption readOption) {
        return get(Integer.class, readOption);
    }

    @NotNull
    public String getAsString(@NotNull ConfigReadOption readOption) {
        return get(String.class, readOption);
    }

    @NotNull
    public ConfigSection getAsSection(@NotNull ConfigReadOption readOption) {
        return get(ConfigSection.class, readOption)
                .toBuilder()
                .configType(readOption.configType())
                .build();
    }

    @NotNull
    private String applyPlaceholder(@NotNull String line, @NotNull ConfigReadOption readOption) {
        var result = line;
        for (var placeholder : readOption.placeholders()) {
            var placeholderName = placeholder.placeholder();
            if (placeholderName.charAt(0) != '%') {
                placeholderName = "%" + placeholderName + "%";
            }

            result = line.replace(placeholderName, placeholder.value());
        }
        return result;
    }
}
