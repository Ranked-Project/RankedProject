import com.google.inject.Injector;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import net.rankedproject.common.instantiator.impl.NatsInstantiator;
import net.rankedproject.common.packet.sender.PacketSenderImpl;
import net.rankedproject.proto.SendPlayerToServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PacketSenderIntegrationTest {

    private Connection connection;
    private PacketSenderImpl packetSender;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        Injector injector = Mockito.mock(Injector.class);
        NatsInstantiator natsInstantiator = mock(NatsInstantiator.class);
        connection = Nats.connect("nats://localhost:4222");

        when(injector.getInstance(NatsInstantiator.class)).thenReturn(natsInstantiator);
        when(natsInstantiator.get()).thenReturn(connection);

        packetSender = new PacketSenderImpl(injector);
    }

    @Test
    void sendAwaitingRequestsAndParsesResponse() throws Exception {
        String subject = "integration.test.subject." + UUID.randomUUID();
        ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);

        var dispatcher = connection.createDispatcher(queue::offer);
        dispatcher.subscribe(subject);

        var packet = SendPlayerToServer.newBuilder()
                .setServerIdentifier("server-2")
                .setPlayerUuid("")
                .build();

        CompletableFuture<?> sendFuture = packetSender.builder(subject, packet).send();
        assertNotNull(sendFuture);

        Message received = queue.poll(5, TimeUnit.SECONDS);
        assertNotNull(received, "Expected to receive a message from NATS within timeout");
        assertArrayEquals(packet.toByteArray(), received.getData(), "Payload bytes should match sent packet");
    }
}
