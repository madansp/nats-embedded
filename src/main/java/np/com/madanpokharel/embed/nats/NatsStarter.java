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

import de.flapdoodle.embed.process.config.SupportConfig;
import de.flapdoodle.embed.process.types.RunningProcessFactory;
import de.flapdoodle.os.Platform;
import de.flapdoodle.reverse.StateID;

/**
 * <p>NatsStarter class.</p>
 *
 * @author madanpokharel
 * @version $Id: $Id
 */
public class NatsStarter extends NatsServerStarter<NatsProcess> {
    /** {@inheritDoc} */
    @Override
    public StateID<NatsProcess> destination() {
        return StateID.of(NatsProcess.class);
    }


    static NatsStarter defaults() {
        return new NatsStarter();
    }

    /** {@inheritDoc} */
    @Override
    protected RunningProcessFactory<NatsProcess> factory(SupportConfig supportConfig, Platform platform, ServerType serverType) {
        return NatsProcess.factory(supportConfig, platform, serverType);
    }
}
