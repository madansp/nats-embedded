package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.ExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Starter;

/**
 * <p>NatsServerStarter class.</p>
 *
 * @author Madan Pokharel
 *
 */
final class NatsServerStarter extends Starter<NatsServerConfig, NatsServerExecutable, NatsServerProcess> {
    /**
     * <p>Constructor for NatsServerStarter.</p>
     *
     * @param config a {@link de.flapdoodle.embed.process.config.RuntimeConfig} object.
     */
    public NatsServerStarter(RuntimeConfig config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NatsServerExecutable newExecutable(NatsServerConfig config, Distribution distribution, RuntimeConfig iRuntimeConfig, ExtractedFileSet iExtractedFileSet) {
        return new NatsServerExecutable(distribution, config, iRuntimeConfig, iExtractedFileSet);
    }
}
