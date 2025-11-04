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
public class RequestFactory {

    protected static final String BASE_URL = Optional
            .ofNullable(System.getenv("REST_API_URL"))
            .orElse("http://localhost:8080/");

    private final Map<RequestType, Function<RequestContent, Request>> requests = new EnumMap<>(RequestType.class);

    @Inject
    private RequestFactory() {
        registerDefaults();
    }

    /**
     * Registers a request function for a specific request type.
     *
     * @param type     The type of request to register.
     * @param function The function that builds the request.
     */
    public void register(RequestType type, Function<RequestContent, Request> function) {
        this.requests.put(type, function);
    }

    /**
     * Retrieves a request of the given type.
     *
     * @param type The type of request to retrieve.
     * @return The constructed request.
     */
    public Request get(RequestType type) {
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
    public Request get(RequestType type, Consumer<HttpUrl.Builder> httpBuilder, Consumer<Request.Builder> requestBuilder) {
        return requests.get(type).apply(new RequestContent(httpBuilder, requestBuilder));
    }

    /**
     * Retrieves a request with optional consumers for modifying the HTTP URL and request builder.
     *
     * @param type           The request type.
     * @param requestContent DTO class containing information to modify the output request.
     * @return The constructed request.
     */
    public Request get(RequestType type, RequestContent requestContent) {
        return requests.get(type).apply(requestContent);
    }

    /**
     * Retrieves a request with an HTTP URL builder.
     *
     * @param type        The request type.
     * @param httpBuilder Consumer to modify the HTTP URL.
     * @return The constructed request.
     */
    public Request getWithHttpBuilder(RequestType type, Consumer<HttpUrl.Builder> httpBuilder) {
        return requests.get(type).apply(new RequestContent(httpBuilder, null));
    }

    /**
     * Retrieves a request with a request builder.
     *
     * @param type           The request type.
     * @param requestBuilder Consumer to modify the request builder.
     * @return The constructed request.
     */
    public Request getWithRequestBuilder(RequestType type, Consumer<Request.Builder> requestBuilder) {
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
    private Request.Builder composeFlexibleRequestBuilder(
            @Nullable RequestContent requestContent,
            @NotNull HttpUrl.Builder urlBuilder
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
