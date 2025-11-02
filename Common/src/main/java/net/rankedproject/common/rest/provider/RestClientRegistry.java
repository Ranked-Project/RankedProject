package net.rankedproject.common.rest.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rankedproject.common.registry.BaseRegistry;
import net.rankedproject.common.rest.RestClient;
import net.rankedproject.common.rest.impl.RankedPlayerRestClient;
import net.rankedproject.common.rest.request.RequestFactory;

import java.util.IdentityHashMap;

@Singleton
public class RestClientRegistry extends BaseRegistry<Class<? extends RestClient<?>>, Class<? extends RestClient<?>>> {

    @Inject
    public RestClientRegistry() {
        super(new IdentityHashMap<>());
    }

    @Override
    public void registerDefaults() {
        register(RankedPlayerRestClient.class, RankedPlayerRestClient.class);
    }
}