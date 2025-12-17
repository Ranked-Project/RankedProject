package net.rankedproject.common.rest.request;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rankedproject.common.rest.request.type.RequestContent;
import net.rankedproject.common.rest.request.type.RequestType;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Factory class for creating and managing HTTP requests using OkHttp.
 * Supports dynamic request composition using consumers for URL and request building.
 */
@Singleton
public final class RequestFactory {

    private static final String BASE_URL = Optional
            .ofNullable(System.getenv("REST_API_URL"))
            .orElse("http://localhost:8080/");

    private final Map<RequestType, Function<RequestContent, Request>> requests = new EnumMap<>(RequestType.class);

    @Inject
    private RequestFactory() {
        registerDefaults();
    }

    /**
     * Registers a request factory for a specific request type.
     *
     * @param type     The type of request to register.
     * @param factory The factory that builds the request.
     */
    public void register(final @NotNull RequestType type, final @NotNull Function<RequestContent, Request> factory) {
        requests.put(type, factory);
    }

    /**
     * Retrieves a request of the given type.
     *
     * @param type The type of request to retrieve.
     * @return The constructed request.
     */
    public @NotNull Request get(final @NotNull RequestType type) {
        return requests.get(type).apply(null);
    }

    /**
     * Retrieves a request with optional consumers for modifying the HTTP URL and request builder.
     *
     * @param type           The request type.
     * @param httpBuilder    Consumer to modify the HTTP URL.
     * @param requestBuilder Consumer to modify the request builder.
     * @return The constructed request.
     */
    public @NotNull Request get(
            final @NotNull RequestType type,
            final @NotNull Consumer<HttpUrl.Builder> httpBuilder,
            final @NotNull Consumer<Request.Builder> requestBuilder
    ) {
        return requests.get(type).apply(new RequestContent(httpBuilder, requestBuilder));
    }

    /**
     * Retrieves a request with optional consumers for modifying the HTTP URL and request builder.
     *
     * @param type           The request type.
     * @param requestContent DTO class containing information to modify the output request.
     * @return The constructed request.
     */
    public @NotNull Request get(final @NotNull RequestType type, final @NotNull RequestContent requestContent) {
        return requests.get(type).apply(requestContent);
    }

    /**
     * Retrieves a request with an HTTP URL builder.
     *
     * @param type        The request type.
     * @param httpBuilder Consumer to modify the HTTP URL.
     * @return The constructed request.
     */
    public @NotNull Request getWithHttpBuilder(
            final @NotNull RequestType type,
            final @NotNull Consumer<HttpUrl.Builder> httpBuilder
    ) {
        return requests.get(type).apply(new RequestContent(httpBuilder, null));
    }

    /**
     * Retrieves a request with a request builder.
     *
     * @param type           The request type.
     * @param requestBuilder Consumer to modify the request builder.
     * @return The constructed request.
     */
    public @NotNull Request getWithRequestBuilder(
            final @NotNull RequestType type,
            final @NotNull Consumer<Request.Builder> requestBuilder
    ) {
        return requests.get(type).apply(new RequestContent(null, requestBuilder));
    }

    private void registerDefaults() {
        for(RequestType type : RequestType.values()) {
            register(type, content -> {
                HttpUrl.Builder urlBuilder = HttpUrl.get(BASE_URL).newBuilder();
                Request.Builder requestBuilder = composeFlexibleRequestBuilder(content, urlBuilder);
                return requestBuilder.build();
            });
        }
    }

    /**
     * Composes a flexible request builder by applying optional consumers for modifying the URL and request.
     *
     * @param requestContent DTO class containing information to modify the output request
     * @param urlBuilder     The URL builder to be modified.
     * @return A configured Request.Builder instance.
     */
    private @NotNull Request.Builder composeFlexibleRequestBuilder(
            final @Nullable RequestContent requestContent,
            final @NotNull HttpUrl.Builder urlBuilder
    ) {
        Request.Builder nativeBuilder = new Request.Builder();

        if (requestContent != null) {
            Consumer<HttpUrl.Builder> httpBuilder = requestContent.httpBuilder();
            if (httpBuilder != null) {
                httpBuilder.accept(urlBuilder);
            }

            Consumer<Request.Builder> requestBuilder = requestContent.requestBuilder();
            if (requestBuilder != null) {
                requestBuilder.accept(nativeBuilder);
            }
        }

        return nativeBuilder
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json");
    }
}
