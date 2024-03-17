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
 * <p>EmbeddedNatsConfig class.</p>
 *
 * @author Madan Pokharel
 * @version $Id: $Id
 */
public final class EmbeddedNatsConfig {
    final String downloadPath;
    final String downloadUserAgent;
    final String artifactStorePath;
    final String extractDirectory;
    private final NatsServerConfig serverConfig;

    private EmbeddedNatsConfig(String downloadPath,
                               String downloadUserAgent,
                               String artifactStorePath,
                               String extractDirectory,
                               NatsServerConfig serverConfig) {
        this.downloadPath = downloadPath;
        this.downloadUserAgent = downloadUserAgent;
        this.serverConfig = serverConfig;
        this.artifactStorePath = artifactStorePath;
        this.extractDirectory = extractDirectory;
    }

    /**
     * <p>defaultNatsServerConfig.</p>
     *
     * @return a {@link np.com.madanpokharel.embed.nats.EmbeddedNatsConfig} object.
     */
    public static EmbeddedNatsConfig defaultNatsServerConfig() {
        return new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS)
                                .withRandomPort()
                                .build()
                )
                .build();
    }

    /**
     * <p>defaultNatsStreamingConfig.</p>
     *
     * @return a {@link np.com.madanpokharel.embed.nats.EmbeddedNatsConfig} object.
     */
    public static EmbeddedNatsConfig defaultNatsStreamingConfig() {
        return new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS_STREAMING)
                                .withRandomPort()
                                .build()
                )
                .build();
    }


    /**
     * <p>Getter for the field <code>serverConfig</code>.</p>
     *
     * @return a {@link np.com.madanpokharel.embed.nats.NatsServerConfig} object.
     */
    public NatsServerConfig getServerConfig() {
        return serverConfig;
    }

    public static class Builder {
        private String downloadUserAgent;
        private String artifactStorePath;
        private String extractDirectory;
        private NatsServerConfig serverConfig;

        public Builder() {
            this.artifactStorePath = ".embedded-nats";
            this.extractDirectory = "extracted";
            this.downloadUserAgent = "Mozilla/5.0 (compatible; Nats Embedded; +https://github.com/madansp/nats-embedded)";
        }

        public Builder withDownloadUserAgent(String userAgent) {
            this.downloadUserAgent = userAgent;
            return this;
        }

        public Builder withArtifactStorePath(String artifactStorePath) {
            this.artifactStorePath = artifactStorePath;
            return this;
        }

        public Builder withExtractDirectory(String extractDirectory) {
            this.extractDirectory = extractDirectory;
            return this;
        }

        public Builder withNatsServerConfig(NatsServerConfig serverConfig) {
            this.serverConfig = serverConfig;
            return this;
        }

        public EmbeddedNatsConfig build() {
            Objects.requireNonNull(serverConfig, "Nats server config cannot be null");
            String downloadPath = "https://github.com/nats-io/" + serverConfig.getServerType().getServerName() + "/releases/download/";
            return new EmbeddedNatsConfig(downloadPath, downloadUserAgent, artifactStorePath, extractDirectory, serverConfig);
        }
    }
}
