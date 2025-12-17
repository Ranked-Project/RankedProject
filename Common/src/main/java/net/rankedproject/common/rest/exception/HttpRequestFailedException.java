package net.rankedproject.common.rest.exception;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public final class HttpRequestFailedException extends RuntimeException {

    public HttpRequestFailedException(final @NotNull IOException exception) {
        super(exception);
    }

    public HttpRequestFailedException(final @NotNull String message, final @NotNull IOException exception) {
        super(message, exception);
    }
}
