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

import de.flapdoodle.embed.process.config.DownloadConfig;
import de.flapdoodle.embed.process.config.SupportConfig;
import de.flapdoodle.embed.process.config.store.Package;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Version;
import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.PersistentDir;
import de.flapdoodle.embed.process.io.directories.TempDir;
import de.flapdoodle.embed.process.io.progress.ProgressListener;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.process.store.ContentHashExtractedFileSetStore;
import de.flapdoodle.embed.process.store.DownloadCache;
import de.flapdoodle.embed.process.store.ExtractedFileSetStore;
import de.flapdoodle.embed.process.store.LocalDownloadCache;
import de.flapdoodle.embed.process.transitions.*;
import de.flapdoodle.embed.process.types.*;
import de.flapdoodle.os.CommonOS;
import de.flapdoodle.os.Platform;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.Transitions;
import de.flapdoodle.reverse.transitions.Derive;
import de.flapdoodle.reverse.transitions.Start;

import java.util.Collections;
import java.util.Objects;


/**
 * <p>EmbeddedNatsServer class.</p>
 *
 * @author Madan Pokharel
 * @version $Id: $Id
 */
public final class EmbeddedNatsServer {
    private final EmbeddedNatsConfig config;

    private TransitionWalker.ReachedState<NatsProcess> runningProcess;

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
        runningProcess = transitions().walker().initState(StateID.of(NatsProcess.class));
    }


    /**
     * <p>isServerRunning.</p>
     *
     * @return a boolean.
     */
    public boolean isServerRunning() {
        return !Objects.isNull(runningProcess) && runningProcess.current().isAlive();
    }


    /**
     * <p>stopServer.</p>
     */
    public void stopServer() {
        runningProcess.close();
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


    private Transitions transitions() {
        NatsPackageResolver resolver = new NatsPackageResolver(config.getServerConfig().getServerType());
        Transitions transitions = Transitions.from(
                InitTempDirectory.withPlatformTempRandomSubDir(),

                Start.to(EmbeddedNatsConfig.class).providedBy(() -> config),

                Derive.given(EmbeddedNatsConfig.class)
                        .state(PersistentDir.class)
                        .deriveBy(embeddedNatsConfig -> PersistentDir.inUserHome(embeddedNatsConfig.artifactStorePath).mapToUncheckedException(RuntimeException::new).get()),

                Derive.given(TempDir.class)
                        .state(ProcessWorkingDir.class)
                        .with(Directories.deleteOnTearDown(
                                TempDir.createDirectoryWith("workingDir"),
                                ProcessWorkingDir::of)
                        ),

                Derive.given(PersistentDir.class)
                        .state(DownloadCache.class)
                        .deriveBy(storeBaseDir -> new LocalDownloadCache(storeBaseDir.value().resolve("archives")))
                        .withTransitionLabel("downloadCache"),


                Derive.given(PersistentDir.class)
                        .state(ExtractedFileSetStore.class)
                        .deriveBy(baseDir -> new ContentHashExtractedFileSetStore(baseDir.value().resolve(config.extractDirectory)))
                        .withTransitionLabel("extractedFileSetStore"),

                Start.to(Name.class).initializedWith(Name.of("nats")).withTransitionLabel("create Name"),

                Start.to(SupportConfig.class).initializedWith(
                        SupportConfig.builder().name("nats-server")
                                .supportUrl("https://github.com/madansp/nats-embedded")
                                .messageOnException((clazz, ex) -> "Open a bug report at https://github.com/madansp/nats-embedded")
                                .build()
                ).withTransitionLabel("create default"),

                Start.to(ProcessConfig.class).initializedWith(ProcessConfig.defaults()).withTransitionLabel("create default"),
                Start.to(ProcessEnv.class).initializedWith(ProcessEnv.of(Collections.emptyMap())).withTransitionLabel("create empty env"),

                Start.to(Version.class).initializedWith(config.getServerConfig().version()).withTransitionLabel("set version"),

                Derive.given(Name.class).state(ProcessOutput.class)
                        .deriveBy(name ->
                                ProcessOutput.builder()
                                        .output(Processors.silent())
                                        .error(Processors.namedConsole("[" + name.value() + " output]"))
                                        .commands(Processors.console())
                                        .build()
                        )
                        .withTransitionLabel("create named console"),

                Start.to(ProgressListener.class)
                        .providedBy(StandardConsoleProgressListener::new)
                        .withTransitionLabel("progressListener"),

                Start.to(ProcessArguments.class).initializedWith(ProcessArguments.of(config.getServerConfig().getConfigList()))
                        .withTransitionLabel("create arguments"),

                Start.to(ServerType.class).providedBy(() -> config.getServerConfig().getServerType()),

                Derive.given(Version.class).state(Distribution.class)
                        .deriveBy(version -> Distribution.detectFor(CommonOS.list(), version))
                        .withTransitionLabel("version + platform"),

                Start.to(Platform.class).providedBy(() -> Platform.detect(CommonOS.list())),

                PackageOfDistribution.with(dist -> Package.builder()
                        .archiveType(ArchiveType.ZIP)
                        .fileSet(resolver.getExecutableFileSet(dist))
                        .url(config.downloadPath + resolver.getPath(dist))
                        .build()),

                DownloadPackage.builder()
                        .downloadConfig(DownloadConfig.builder().userAgent(config.downloadUserAgent).build())
                        .build(),

                ExtractPackage.withDefaults().withExtractedFileSetStore(StateID.of(ExtractedFileSetStore.class)),

                NatsStarter.defaults()
        );

        return transitions;
    }

}
