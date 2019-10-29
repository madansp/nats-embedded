package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.DownloadConfigBuilder;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.ExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.process.store.IArtifactStore;

import java.util.Objects;

/**
 * <p>EmbeddedNatsConfig class.</p>
 *
 * @author Madan Pokharel
 *
 */
public final class EmbeddedNatsConfig {
    private String downloadPath;
    private String downloadUserAgent;
    private IDirectory artifactStorePath;
    private IDirectory extractDirectory;
    private NatsServerConfig serverConfig;

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
     * @return a {@link de.flapdoodle.embed.process.config.IRuntimeConfig} object.
     */
    IRuntimeConfig getRunTimeConfig() {

        ProcessOutput processOutput = new ProcessOutput(Processors.silent(),
                Processors.namedConsole("[" + serverConfig.getServerType().getServerName() + "]"), Processors.console());

        return new RuntimeConfigBuilder()
                .processOutput(processOutput)
                .commandLinePostProcessor(new ICommandLinePostProcessor.Noop())
                .artifactStore(artifactStore())
                .build();
    }

    private IDownloadConfig downloadConfig() {
        DownloadConfigBuilder downloadConfigBuilder = new DownloadConfigBuilder();
        downloadConfigBuilder.fileNaming(new UUIDTempNaming())
                .downloadPath(downloadPath)
                .progressListener(new StandardConsoleProgressListener())
                .artifactStorePath(artifactStorePath)
                .downloadPrefix(serverConfig.getServerType().getServerName() + "-download")
                .userAgent(downloadUserAgent);

        downloadConfigBuilder.packageResolver(new NatsPackageResolver(serverConfig.getServerType()));


        return downloadConfigBuilder.build();
    }

    private IArtifactStore artifactStore() {
        return new ExtractedArtifactStoreBuilder()
                .extractDir(extractDirectory)
                .extractExecutableNaming((prefix, postfix) -> postfix)
                .tempDir(new PropertyOrPlatformTempDir())
                .executableNaming(new UUIDTempNaming())
                .download(downloadConfig())
                .downloader(new Downloader())
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
