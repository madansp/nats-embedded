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

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.Assert.*;

public class EmbeddedNatsTest {

    @Test
    public void testNatsWithServerConfig() throws Exception {
        int port = 4566;
        EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS)
                                .withPort(port)
                                .withNatsVersion(NatsVersion.LATEST)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
        natsServer.startServer();

        assertTrue(natsServer.isServerRunning());
        assertEquals(port, natsServer.getRunningPort());

        natsServer.stopServer();

        assertFalse(natsServer.isServerRunning());
    }

    @Test
    public void testNatsWithDefaultConfig() throws Exception {
        EmbeddedNatsConfig config = EmbeddedNatsConfig.defaultNatsServerConfig();
        EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
        natsServer.startServer();

        assertTrue(natsServer.isServerRunning());

        natsServer.stopServer();

        assertFalse(natsServer.isServerRunning());
    }

    @Test
    public void testNatsPublishSubscribe() throws Exception {

        EmbeddedNatsConfig config = EmbeddedNatsConfig.defaultNatsServerConfig();
        EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
        natsServer.startServer();

        Connection nc = Nats.connect(natsServer.getNatsUrl());

        String subject = "mySubject";
        Subscription sub = nc.subscribe(subject);

        String publishedMessage = "hello world";
        nc.publish(subject, publishedMessage.getBytes(StandardCharsets.UTF_8));

        Message msg = sub.nextMessage(Duration.ofMillis(500));

        String response = new String(msg.getData(), StandardCharsets.UTF_8);

        assertEquals(publishedMessage, response);

        natsServer.stopServer();

    }
}
