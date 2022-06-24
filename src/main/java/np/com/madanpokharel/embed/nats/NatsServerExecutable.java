package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.ExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;

import java.io.IOException;

/**
 * <p>NatsServerExecutable class.</p>
 *
 * @author Madan Pokharel
 *
 */
final class NatsServerExecutable extends Executable<NatsServerConfig, NatsServerProcess> {

    NatsServerExecutable(Distribution distribution, NatsServerConfig config,
                         RuntimeConfig runtimeConfig, ExtractedFileSet files) {
        super(distribution, config, runtimeConfig, files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NatsServerProcess start(Distribution distribution, NatsServerConfig config, RuntimeConfig runtime) throws IOException {
        return new NatsServerProcess(distribution, config, runtime, this);
    }

}
