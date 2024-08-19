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

import de.flapdoodle.embed.process.archives.ExtractedFileSet;
import de.flapdoodle.embed.process.config.SupportConfig;
import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.embed.process.types.*;
import de.flapdoodle.os.Platform;
import de.flapdoodle.reverse.State;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.StateLookup;
import de.flapdoodle.reverse.Transition;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Abstract NatsServerStarter class.</p>
 *
 * @author madanpokharel
 * @version $Id: $Id
 */
public abstract class NatsServerStarter<T extends RunningProcess> implements Transition<T> {
    /**
     * <p>processExecutable.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ExtractedFileSet> processExecutable() {
        return StateID.of(ExtractedFileSet.class);
    }

    /**
     * <p>processWorkingDir.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ProcessWorkingDir> processWorkingDir() {
        return StateID.of(ProcessWorkingDir.class);
    }

    /**
     * <p>processConfig.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ProcessConfig> processConfig() {
        return StateID.of(ProcessConfig.class);
    }

    /**
     * <p>processEnv.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ProcessEnv> processEnv() {
        return StateID.of(ProcessEnv.class);
    }

    /**
     * <p>arguments.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ProcessArguments> arguments() {
        return StateID.of(ProcessArguments.class);
    }

    /**
     * <p>processOutput.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ProcessOutput> processOutput() {
        return StateID.of(ProcessOutput.class);
    }

    /**
     * <p>supportConfig.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<SupportConfig> supportConfig() {
        return StateID.of(SupportConfig.class);
    }

    /**
     * <p>platform.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<Platform> platform() {
        return StateID.of(Platform.class);
    }

    /**
     * <p>serverType.</p>
     *
     * @return a {@link de.flapdoodle.reverse.StateID} object
     */
    public StateID<ServerType> serverType() {
        return StateID.of(ServerType.class);
    }


    /** {@inheritDoc} */
    @Override
    public Set<StateID<?>> sources() {
        return StateID.setOf(
                processWorkingDir(),
                processExecutable(),
                processConfig(),
                processEnv(),
                arguments(),
                processOutput(),
                supportConfig(),
                platform(),
                serverType()
        );
    }

    /**
     * <p>factory.</p>
     *
     * @param supportConfig a {@link de.flapdoodle.embed.process.config.SupportConfig} object
     * @param platform a {@link de.flapdoodle.os.Platform} object
     * @param serverType a {@link np.com.madanpokharel.embed.nats.ServerType} object
     * @return a {@link de.flapdoodle.embed.process.types.RunningProcessFactory} object
     */
    protected abstract RunningProcessFactory<T> factory(SupportConfig supportConfig, Platform platform, ServerType serverType);

    /** {@inheritDoc} */
    @Override
    public State<T> result(StateLookup lookup) {
        Path processWorkingDir = lookup.of(processWorkingDir()).value();
        ExtractedFileSet fileSet = lookup.of(processExecutable());
        List<String> arguments = lookup.of(arguments()).value();
        Map<String, String> environment = lookup.of(processEnv()).value();
        ProcessConfig processConfig = lookup.of(processConfig());
        ProcessOutput processOutput = lookup.of(processOutput());
        SupportConfig supportConfig = lookup.of(supportConfig());
        Platform platform = lookup.of(platform());
        ServerType serverType = lookup.of(serverType());

        try {
            RunningProcessFactory<T> factory = factory(supportConfig, platform, serverType);

            T running = RunningProcess.start(factory, processWorkingDir, fileSet.executable(), arguments, environment, processConfig,
                    processOutput, supportConfig);

            return State.of(running, RunningProcess::stop);
        } catch (IOException ix) {
            String hint = "";
            if (ix.getMessage().contains("Bad CPU type in executable")) {
                hint = " - " + platform.toString();
            }
            throw new RuntimeException("could not start process" + hint, ix);
        }
    }

}
