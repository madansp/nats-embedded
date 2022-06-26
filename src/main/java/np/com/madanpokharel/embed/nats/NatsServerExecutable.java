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
