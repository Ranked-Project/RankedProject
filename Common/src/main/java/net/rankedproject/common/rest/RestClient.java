package net.rankedproject.common.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.rest.request.RequestFactory;
import net.rankedproject.common.rest.request.type.RequestContent;
import net.rankedproject.common.rest.request.type.RequestType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class representing a generic REST client.
 * Provides basic HTTP operations with retry mechanisms.
 *
 * @param <V> the type of the value being sent and received
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public abstract class RestClient<V> implements RestCrudAPI<V> {

    private static final int MAX_RETRY_ATTEMPTS = 5;

    protected static final Logger LOGGER = Logger.getLogger(RestClient.class.getName());
    protected static final Gson GSON = new GsonBuilder().serializeNulls().create();

    protected static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build();

    protected final RequestFactory requestFactory;

    /**
     * Executes a GET request and returns the response as a JsonElement.
     *
     * @param request the HTTP request to execute
     * @return the parsed JSON response, or null if unsuccessful
     */
    public JsonElement get(Request request) {
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                LOGGER.warning("GET request failed: " + response.code());
                return null;
            }
            return GSON.fromJson(response.body().string(), JsonElement.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve resource", e);
        }
    }

    /**
     * Executes a GET request and returns the response as a JsonElement.
     *
     * @return the parsed JSON response, or null if unsuccessful
     */
    public JsonElement retrieve() {
        Request request = requestFactory.get(RequestType.GET);
        return get(request);
    }

    /**
     * Executes a GET request and returns the response as a JsonElement.
     *
     * @param requestContent DTO class containing information to modify the output request.
     * @return the parsed JSON response, or null if unsuccessful
     */
    public JsonElement retrieve(@NotNull RequestContent requestContent) {
        Request request = requestFactory.get(RequestType.GET, requestContent);
        return get(request);
    }

    /**
     * Sends an HTTP Request with retries if it didn't return success
     *
     * @param request the HTTP request to execute
     */
    public void sendRequestWithRetry(@NotNull Request request) {
        executeWithRetry(request, 1);
    }

    /**
     * Sends an HTTP Request with retries if it didn't return success
     *
     * @param requestType the HTTP request type to execute
     */
    public void sendRequestWithRetry(@NotNull RequestType requestType) {
        Request request = requestFactory.get(requestType);
        executeWithRetry(request, 1);
    }

    /**
     * Sends an HTTP Request with retries if it didn't return success
     *
     * @param requestType    the HTTP request type to execute
     * @param requestContent DTO class containing information to modify the output request.
     */
    public void sendRequestWithRetry(
            @NotNull RequestType requestType,
            @NotNull RequestContent requestContent
    ) {
        Request request = requestFactory.get(requestType, requestContent);
        executeWithRetry(request, 1);
    }

    /**
     * Sends an HTTP Request.
     *
     * @param request the HTTP request to execute
     * @return Response containing HTTP information
     */
    @NotNull
    public Response sendRequest(@NotNull Request request) {
        return execute(request);
    }

    /**
     * Sends an HTTP Request.
     *
     * @param requestType the HTTP request type to execute
     * @return Response containing HTTP information
     */
    @NotNull
    public Response sendRequest(@NotNull RequestType requestType) {
        return execute(requestFactory.get(requestType));
    }

    /**
     * Sends an HTTP Request.
     *
     * @param requestType    the HTTP request type to execute
     * @param requestContent DTO class containing information to modify the output request.
     * @return Response containing HTTP information
     */
    @NotNull
    public Response sendRequest(
            @NotNull RequestType requestType,
            @NotNull RequestContent requestContent
    ) {
        return execute(requestFactory.get(requestType, requestContent));
    }

    /**
     * Executes a request with retry logic.
     *
     * @param request        the HTTP request to execute
     * @param attemptedTimes attempts taken to send a request
     */
    private void executeWithRetry(@NotNull Request request, int attemptedTimes) {
        if (attemptedTimes >= MAX_RETRY_ATTEMPTS) {
            LOGGER.warning("Attempt #" + attemptedTimes + " failed sending request: " + request);
            return;
        }

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                executeWithRetry(request, attemptedTimes + 1);
            }
        } catch (IOException e) {
            throw new RuntimeException("Request execution failed", e);
        }
    }

    @NotNull
    private Response execute(@NotNull Request request) {
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
