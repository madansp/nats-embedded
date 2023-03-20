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

import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.DownloadConfig;
import de.flapdoodle.embed.process.config.store.ImmutableDownloadConfig;
import de.flapdoodle.embed.process.extract.DirectoryAndExecutableNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.Directory;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.process.runtime.CommandLinePostProcessor;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.ExtractedArtifactStore;
import de.flapdoodle.embed.process.store.IArtifactStore;

import java.util.Objects;

/**
 * <p>EmbeddedNatsConfig class.</p>
 *
 * @author Madan Pokharel
 *
 */
public final class EmbeddedNatsConfig {
    private final String downloadPath;
    private final String downloadUserAgent;
    private final Directory artifactStorePath;
    private final Directory extractDirectory;
    private final NatsServerConfig serverConfig;

    private EmbeddedNatsConfig(String downloadPath, String downloadUserAgent,
                               String artifactStorePath, String extractDirectory, NatsServerConfig serverConfig) {
        this.downloadPath = downloadPath;
        this.downloadUserAgent = downloadUserAgent;
        this.artifactStorePath = new UserHome(artifactStorePath);
        this.extractDirectory = new UserHome(extractDirectory);
        this.serverConfig = serverConfig;
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
     * <p>getRunTimeConfig.</p>
     *
     * @return a {@link de.flapdoodle.embed.process.config.RuntimeConfig} object.
     */
    RuntimeConfig getRunTimeConfig() {

        ProcessOutput processOutput = new ProcessOutput(Processors.silent(),
                Processors.namedConsole("[" + serverConfig.getServerType().getServerName() + "]"), Processors.console());

        return RuntimeConfig.builder()
                .processOutput(processOutput)
                .commandLinePostProcessor(new CommandLinePostProcessor.Noop())
                .artifactStore(artifactStore())
                .build();
    }

    private DownloadConfig downloadConfig() {
        ImmutableDownloadConfig.Builder downloadConfigBuilder = DownloadConfig.builder();
        downloadConfigBuilder.fileNaming(new UUIDTempNaming())
                .downloadPath((__) -> downloadPath)
                .progressListener(new StandardConsoleProgressListener())
                .artifactStorePath(artifactStorePath)
                .downloadPrefix(serverConfig.getServerType().getServerName() + "-download")
                .userAgent(downloadUserAgent);

        downloadConfigBuilder.packageResolver(new NatsPackageResolver(serverConfig.getServerType()));


        return downloadConfigBuilder.build();
    }

    private IArtifactStore artifactStore() {
        return ExtractedArtifactStore.builder()
            .extraction(DirectoryAndExecutableNaming.of(extractDirectory, (prefix, postfix) -> postfix))
            .temp(DirectoryAndExecutableNaming.of(PropertyOrPlatformTempDir.defaultInstance(), new UUIDTempNaming()))
            .downloadConfig(downloadConfig())
            .downloader(Downloader.platformDefault())
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
            this.extractDirectory = ".embedded-nats/extracted";
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
