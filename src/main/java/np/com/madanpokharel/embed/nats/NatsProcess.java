/*
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.SupportConfig;
import de.flapdoodle.embed.process.io.StreamProcessor;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.process.types.RunningProcessFactory;
import de.flapdoodle.os.Platform;

import java.nio.file.Path;

/**
 * <p>NatsProcess class.</p>
 *
 * @author madanpokharel
 * @version $Id: $Id
 */
public class NatsProcess extends RunningNatsProcess {

    /**
     * <p>Constructor for NatsProcess.</p>
     *
     * @param process a {@link de.flapdoodle.embed.process.runtime.ProcessControl} object
     * @param pidFile a {@link java.nio.file.Path} object
     * @param onStop a {@link java.lang.Runnable} object
     * @param supportConfig a {@link de.flapdoodle.embed.process.config.SupportConfig} object
     * @param platform a {@link de.flapdoodle.os.Platform} object
     * @param commandOutput a {@link de.flapdoodle.embed.process.io.StreamProcessor} object
     * @param natsProcessId a int
     * @param serverType a {@link np.com.madanpokharel.embed.nats.ServerType} object
     */
    public NatsProcess(
            ProcessControl process,
            Path pidFile,
            Runnable onStop,
            SupportConfig supportConfig,
            Platform platform,
            StreamProcessor commandOutput,
            int natsProcessId,
            ServerType serverType
    ) {
        super("natsProcess", process, pidFile, onStop, supportConfig, platform, commandOutput, natsProcessId, serverType);
    }

    /**
     * <p>factory.</p>
     *
     * @param supportConfig a {@link de.flapdoodle.embed.process.config.SupportConfig} object
     * @param platform a {@link de.flapdoodle.os.Platform} object
     * @param serverType a {@link np.com.madanpokharel.embed.nats.ServerType} object
     * @return a {@link de.flapdoodle.embed.process.types.RunningProcessFactory} object
     */
    public static RunningProcessFactory<NatsProcess> factory(SupportConfig supportConfig, Platform platform, ServerType serverType) {
        return RunningNatsProcess.factory(NatsProcess::new, supportConfig, platform, serverType);
    }
}
