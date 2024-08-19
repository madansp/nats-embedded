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
import de.flapdoodle.embed.process.io.*;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.process.runtime.Processes;
import de.flapdoodle.embed.process.types.RunningProcessFactory;
import de.flapdoodle.embed.process.types.RunningProcessImpl;
import de.flapdoodle.os.Platform;
import de.flapdoodle.types.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Abstract RunningNatsProcess class.</p>
 *
 * @author madanpokharel
 * @version $Id: $Id
 */
public abstract class RunningNatsProcess extends RunningProcessImpl {

    private static Logger LOGGER = LoggerFactory.getLogger(RunningNatsProcess.class);

    private final String commandName;
    private final SupportConfig supportConfig;
    private final Platform platform;
    private final StreamProcessor commandOutput;
    private final int natsProcessId;
    private boolean shutDownCommandAlreadyExecuted = false;

    /**
     * <p>Constructor for RunningNatsProcess.</p>
     *
     * @param commandName a {@link java.lang.String} object
     * @param process a {@link de.flapdoodle.embed.process.runtime.ProcessControl} object
     * @param pidFile a {@link java.nio.file.Path} object
     * @param onStop a {@link java.lang.Runnable} object
     * @param supportConfig a {@link de.flapdoodle.embed.process.config.SupportConfig} object
     * @param platform a {@link de.flapdoodle.os.Platform} object
     * @param commandOutput a {@link de.flapdoodle.embed.process.io.StreamProcessor} object
     * @param natsProcessId a int
     * @param serverType a {@link np.com.madanpokharel.embed.nats.ServerType} object
     */
    protected RunningNatsProcess(
            String commandName,
            ProcessControl process,
            Path pidFile,
            Runnable onStop,
            SupportConfig supportConfig,
            Platform platform,
            StreamProcessor commandOutput,
            int natsProcessId,
            ServerType serverType
    ) {
        super(process, pidFile, 5000, onStop);
        this.commandName = commandName;
        this.supportConfig = supportConfig;
        this.platform = platform;
        this.commandOutput = commandOutput;
        this.natsProcessId = natsProcessId;
    }

    /** {@inheritDoc} */
    @Override
    public int stop() {
        try {
            stopInternal();
        } finally {
            return super.stop();
        }
    }

    //	@Override
    private void stopInternal() {
        if (isAlive()) {
            if (!sendKillToProcess()) {
                LOGGER.warn("could not stop " + commandName + ", try next");
                if (!sendTermToProcess()) {
                    LOGGER.warn("could not stop " + commandName + ", try next");
                    if (!tryKillToProcess()) {
                        LOGGER.warn("could not stop " + commandName + " the second time, try one last thing");
                    }
                }
            }

        }
    }

    private long getProcessId() {
        return natsProcessId;
    }

    /**
     * <p>sendKillToProcess.</p>
     *
     * @return a boolean
     */
    protected boolean sendKillToProcess() {
        return getProcessId() > 0 && Processes.killProcess(supportConfig, platform,
                StreamToLineProcessor.wrap(commandOutput), getProcessId());
    }

    /**
     * <p>sendTermToProcess.</p>
     *
     * @return a boolean
     */
    protected boolean sendTermToProcess() {
        return getProcessId() > 0 && Processes.termProcess(supportConfig, platform,
                StreamToLineProcessor.wrap(commandOutput), getProcessId());
    }

    /**
     * <p>tryKillToProcess.</p>
     *
     * @return a boolean
     */
    protected boolean tryKillToProcess() {
        return getProcessId() > 0 && Processes.tryKillProcess(supportConfig, platform,
                StreamToLineProcessor.wrap(commandOutput), getProcessId());
    }

    interface InstanceFactory<T extends RunningNatsProcess> {
        T create(ProcessControl process, Path pidFile, Runnable closeAllOutputs, SupportConfig supportConfig, Platform platform, StreamProcessor commands, int pid, ServerType serverType);
    }

    static int getNatsProcessId(String output) {
        Pattern pattern = Pattern.compile("\\[\\d+]");
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            String value = matcher.group(0);
            return Integer.parseInt(value.replace("[", "").replace("]", ""));
        }
        return -1;
    }

    static <T extends RunningNatsProcess> RunningProcessFactory<T> factory(InstanceFactory<T> instanceFactory, SupportConfig supportConfig, Platform platform, ServerType serverType) {
        return (process, processOutput, pidFile, timeout) -> {

            SuccessMessageLineListener logWatch = SuccessMessageLineListener.of(successMessage(serverType), knownFailureMessages(), "error");
            ReaderProcessor output = Processors.connect(process.getReader(), new ListeningStreamProcessor(StreamToLineProcessor.wrap(processOutput.output()), logWatch::inspect));
            ReaderProcessor error = Processors.connect(process.getError(), new ListeningStreamProcessor(StreamToLineProcessor.wrap(processOutput.error()), logWatch::inspect));
            Runnable closeAllOutputs = () -> {
                ReaderProcessor.abortAll(output, error);
            };

            logWatch.waitForResult(5000);
            if (logWatch.successMessageFound()) {
                int pid = getNatsProcessId(logWatch.allLines());
                return instanceFactory.create(process, pidFile, closeAllOutputs, supportConfig, platform, processOutput.commands(), pid, serverType);

            } else {
                String failureFound = logWatch.errorMessage().isPresent()
                        ? logWatch.errorMessage().get() : logWatch.allLines();

                return Try.<T, RuntimeException>supplier(() -> {
                            throw new RuntimeException("Could not start process: " + failureFound);
                        })
                        .andFinally(() -> {
                            process.stop(timeout);
                        })
                        .andFinally(closeAllOutputs)
                        .get();
            }
        };
    }

    // VisibleForTesting

    private static List<String> successMessage(ServerType serverType) {
        if (serverType == ServerType.NATS) {
            return Collections.singletonList("Server is ready");
        }

        return Arrays.asList(
                "Streaming Server is ready",
                "----------------------------------"
        );


    }

    private static List<String> knownFailureMessages() {
        return Arrays.asList(
                "(?<error>Error listening on port)",
                "(?<error>flag provided but not defined)",
                "(?<error>Error parsing command line:.*)",
                "(?<error>address already in use)"
        );
    }

}
