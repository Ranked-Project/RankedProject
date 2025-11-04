package net.rankedproject.common.rest.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rankedproject.common.data.domain.RankedPlayer;
import net.rankedproject.common.rest.request.RequestFactory;
import net.rankedproject.common.rest.type.PlayerRestClient;

@Singleton
public class RankedPlayerRestClient extends PlayerRestClient<RankedPlayer> {

    @Inject
    public RankedPlayerRestClient(RequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    public String getRepository() {
        return "api/v1/ranked/players";
    }

    @Override
    public Class<RankedPlayer> getReturnType() {
        return RankedPlayer.class;
    }
}