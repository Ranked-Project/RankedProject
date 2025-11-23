package net.rankedproject.common.instantiator.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.instantiator.Instantiator;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RedisInstantiator implements Instantiator<RedissonClient> {

    @NotNull
    @Override
    public RedissonClient initInternally() {
        return Redisson.create();
    }
}
