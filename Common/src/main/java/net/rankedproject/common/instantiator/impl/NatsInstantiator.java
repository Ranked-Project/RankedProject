package net.rankedproject.common.instantiator.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.rankedproject.common.instantiator.Instantiator;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NatsInstantiator implements Instantiator<Connection> {

    @NotNull
    @Override
    @SneakyThrows
    public Connection initInternally() {
        return Nats.connect();
    }
}