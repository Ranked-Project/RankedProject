package net.rankedproject.common.rest.request.type;

import java.util.function.Consumer;
import lombok.Builder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

@Builder
public record RequestContent(
        @Nullable Consumer<HttpUrl.Builder> httpBuilder,
        @Nullable Consumer<Request.Builder> requestBuilder
) {
}
