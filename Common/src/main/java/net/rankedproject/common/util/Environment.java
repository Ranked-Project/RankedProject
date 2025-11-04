package net.rankedproject.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * The environment type defines runtime behavior, which is handy for local testing.
 * It can be set by adding a system environment variable named {@code ENVIRONMENT}
 * with a value equal to the environment's identifier.
 *
 * <p>For example, setting the environment variable {@code ENVIRONMENT} with the
 * value {@code TEST} would activate the {@code TESTING} environment.</p>
 */
@Getter
@RequiredArgsConstructor
public enum Environment {

    /**
     * Fakes certain data, usually used for running manual tests locally
     */
    TESTING("TEST"),
    PRODUCTION("PRODUCTION");

    private final String identifier;

    @NotNull
    public static Environment fromIdentifier(@NotNull String identifier) {
        return Arrays.stream(values())
                .filter(environment -> identifier.equalsIgnoreCase(environment.getIdentifier()))
                .findFirst()
                .orElse(Environment.PRODUCTION);
    }
}
