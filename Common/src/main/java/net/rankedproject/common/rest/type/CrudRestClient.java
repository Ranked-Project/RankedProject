package net.rankedproject.common.rest.type;

import com.google.gson.JsonElement;
import java.util.Collection;
import net.rankedproject.common.rest.RestClient;
import net.rankedproject.common.rest.request.RequestFactory;
import net.rankedproject.common.rest.request.type.RequestContent;
import net.rankedproject.common.rest.request.type.RequestType;
import okhttp3.RequestBody;

public abstract class CrudRestClient<V> extends RestClient<V> {

    public CrudRestClient(RequestFactory requestFactory) {
        super(requestFactory);
    }

    public Collection<V> getAll() {
        JsonElement jsonElement = retrieve(RequestContent.builder()
                .httpBuilder(builder -> builder.addPathSegments(getRepository()))
                .build());

        return jsonElement.getAsJsonArray().asList()
                .stream()
                .map(element -> GSON.fromJson(element, getReturnType()))
                .toList();
    }

    public V get(String... params) {
        JsonElement jsonElement = retrieve(RequestContent.builder()
                .httpBuilder(builder -> {
                    builder.addPathSegments(getRepository());
                    for (String param : params) {
                        builder.addPathSegment(param);
                    }
                })
                .build());

        return GSON.fromJson(jsonElement, getReturnType());
    }

    public V get(RequestContent content) {
        JsonElement jsonElement = retrieve(content);
        return GSON.fromJson(jsonElement, getReturnType());
    }

    public void update(V value) {
        sendRequestWithRetry(RequestType.PUT, RequestContent.builder()
                .requestBuilder(builder -> builder.put(RequestBody.create(GSON.toJson(value), JSON)))
                .build());
    }

    public void update(RequestContent content) {
        sendRequestWithRetry(RequestType.PUT, content);
    }

    public void save(V value) {
        sendRequestWithRetry(RequestType.POST, RequestContent.builder()
                .requestBuilder(builder -> builder.post(RequestBody.create(GSON.toJson(value), JSON)))
                .build());
    }

    public void save(RequestContent content) {
        sendRequestWithRetry(RequestType.POST, content);
    }

    public void delete(V value) {
        sendRequestWithRetry(RequestType.DELETE, RequestContent.builder()
                .requestBuilder(builder -> builder.delete(RequestBody.create(GSON.toJson(value), JSON)))
                .build());
    }

    public void delete(RequestContent content) {
        sendRequestWithRetry(RequestType.DELETE, content);
    }
}
