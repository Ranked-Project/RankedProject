package net.rankedproject.common.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class EnvironmentUtil {

    private final Environment ENVIRONMENT = Optional.ofNullable(System.getenv("ENVIRONMENT"))
            .map(Environment::fromIdentifier)
            .orElse(Environment.PRODUCTION);

    /**
     * Checks whether current process is running in testing mode or not.
     * It could be set by adding System Environment Variable named `ENVIRONMENT` with value `TEST`
     *
     * @return True if process is running in testing mode and false if otherwise
     */
    public boolean isTesting() {
        return ENVIRONMENT == Environment.TESTING;
    }
}
