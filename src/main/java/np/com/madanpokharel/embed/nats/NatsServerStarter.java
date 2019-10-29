package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
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
     * @param config a {@link de.flapdoodle.embed.process.config.IRuntimeConfig} object.
     */
    public NatsServerStarter(IRuntimeConfig config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NatsServerExecutable newExecutable(NatsServerConfig config, Distribution distribution, IRuntimeConfig iRuntimeConfig, IExtractedFileSet iExtractedFileSet) {
        return new NatsServerExecutable(distribution, config, iRuntimeConfig, iExtractedFileSet);
    }
}
