/*
 * Copyright 2022 Madan Pokharel
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package np.com.madanpokharel.embed.nats;

import io.nats.streaming.*;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class EmbeddedNatsStreamingTest {

    @Test
    public void testNatsStreamingWithServerConfig() throws Exception {
        int port = 4566;
        EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS_STREAMING)
                                .withPort(port)
                                .withNatsStreamingVersion(NatsStreamingVersion.LATEST)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsStreamingServer = new EmbeddedNatsServer(config);
        natsStreamingServer.startServer();

        assertTrue(natsStreamingServer.isServerRunning());
        assertEquals(port, natsStreamingServer.getRunningPort());

        natsStreamingServer.stopServer();

        assertFalse(natsStreamingServer.isServerRunning());
    }

    @Test
    public void testNatsStreamingWithDefaultConfig() throws Exception {
        EmbeddedNatsConfig config = EmbeddedNatsConfig.defaultNatsStreamingConfig();
        EmbeddedNatsServer nastStreamingServer = new EmbeddedNatsServer(config);
        nastStreamingServer.startServer();

        assertTrue(nastStreamingServer.isServerRunning());

        nastStreamingServer.stopServer();

        assertFalse(nastStreamingServer.isServerRunning());
    }

    @Test
    public void testNatsStreamingPublishSubscribe() throws Exception {

        String clusterId = "my-cluster" + UUID.randomUUID();
        EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS_STREAMING)
                                .withClusterId(clusterId)
                                .withRandomPort()
                                .withNatsStreamingVersion(NatsStreamingVersion.V0_16_2)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsStreamingServer = new EmbeddedNatsServer(config);
        natsStreamingServer.startServer();

        Options options = new Options.Builder()
                .natsUrl(natsStreamingServer.getNatsUrl())
                .clusterId(clusterId)
                .clientId(UUID.randomUUID().toString())
                .build();

        StreamingConnectionFactory cf = new StreamingConnectionFactory(options);
        StreamingConnection sc = cf.createConnection();

        String publishedMessage = "Hello World";
        sc.publish("foo", publishedMessage.getBytes());

        final CountDownLatch doneSignal = new CountDownLatch(1);

        final String[] msg = {""};

        Subscription sub = sc.subscribe("foo", message -> {
            msg[0] = new String(message.getData());
            doneSignal.countDown();
        }, new SubscriptionOptions.Builder().deliverAllAvailable().build());

        doneSignal.await(1, TimeUnit.SECONDS);

        assertEquals(publishedMessage, msg[0]);
        sub.unsubscribe();
        sc.close();

        natsStreamingServer.stopServer();

    }
}
