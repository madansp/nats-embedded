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

import java.util.Objects;


/**
 * <p>EmbeddedNatsServer class.</p>
 *
 * @author Madan Pokharel
 *
 */
public final class EmbeddedNatsServer {
    private final EmbeddedNatsConfig config;

    private NatsServerProcess process;
    private NatsServerExecutable executable;

    /**
     * <p>Constructor for EmbeddedNatsServer.</p>
     *
     * @param config a {@link np.com.madanpokharel.embed.nats.EmbeddedNatsConfig} object.
     */
    public EmbeddedNatsServer(EmbeddedNatsConfig config) {
        Objects.requireNonNull(config, "config cannot be null");
        this.config = config;
    }


    /**
     * <p>startServer.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void startServer() throws Exception {
        NatsServerStarter natsServerStarter = new NatsServerStarter(config.getRunTimeConfig());
        executable = natsServerStarter.prepare(config.getServerConfig());
        process = executable.start();
    }


    /**
     * <p>isServerRunning.</p>
     *
     * @return a boolean.
     */
    public boolean isServerRunning() {
        return !Objects.isNull(process) && process.isProcessRunning();
    }


    /**
     * <p>stopServer.</p>
     */
    public void stopServer() {
        process.stop();
        executable.stop();
    }

    /**
     * <p>getRunningPort.</p>
     *
     * @return an int.
     */
    public int getRunningPort() {
        return config.getServerConfig().getPort();
    }

    /**
     * <p>getRunningHost.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRunningHost() {
        return config.getServerConfig().getHost();
    }

    /**
     * <p>getNatsUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNatsUrl() {
        return "nats://" + getRunningHost() + ":" + getRunningPort();
    }

    /**
     * <p>getClusterId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClusterId() {
        return this.config.getServerConfig().getClusterId();
    }

    /**
     * <p>getServerConfig.</p>
     *
     * @return a {@link np.com.madanpokharel.embed.nats.NatsServerConfig} object.
     */
    public NatsServerConfig getServerConfig() {
        return this.config.getServerConfig();
    }

}
