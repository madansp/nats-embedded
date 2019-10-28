package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;

import java.io.IOException;

/**
 * <p>NatsServerExecutable class.</p>
 *
 * @author Madan Pokharel
 * @version 1.0.0
 */
final class NatsServerExecutable extends Executable<NatsServerConfig, NatsServerProcess> {

    NatsServerExecutable(Distribution distribution, NatsServerConfig config,
                         IRuntimeConfig runtimeConfig, IExtractedFileSet files) {
        super(distribution, config, runtimeConfig, files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NatsServerProcess start(Distribution distribution, NatsServerConfig config, IRuntimeConfig runtime) throws IOException {
        return new NatsServerProcess(distribution, config, runtime, this);
    }

}
