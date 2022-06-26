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
import de.flapdoodle.embed.process.config.process.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.ExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>NatsServerProcess class.</p>
 *
 * @author Madan Pokharel
 *
 */
final class NatsServerProcess extends AbstractProcess<NatsServerConfig, NatsServerExecutable, NatsServerProcess> {

    private boolean stopped;
    private NatsServerConfig serverConfig;

    NatsServerProcess(Distribution distribution, NatsServerConfig config, RuntimeConfig runtimeConfig,
                      NatsServerExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getCommandLine(Distribution distribution, NatsServerConfig config, ExtractedFileSet files) {
        this.serverConfig = config;

        List<String> command = new ArrayList<>(Collections.singletonList(Files.fileOf(files.baseDir(),
                files.executable()).getAbsolutePath()));

        command.addAll(config.getConfigList());
        return command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAfterProcessStart(ProcessControl process, RuntimeConfig runtimeConfig) {
        ProcessOutput outputConfig = runtimeConfig.processOutput();

        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(getSuccessMessage(),
                knownFailureMessages(), StreamToLineProcessor.wrap(outputConfig.error()));

        Processors.connect(process.getError(), logWatch);

        logWatch.waitForResult(5000);

        if (!logWatch.isInitWithSuccess()) {
            throw new RuntimeException("could not start nats proceess");
        }

        setProcessId(getNatsProcessId(logWatch.getOutput()));

    }

    /**
     * <p>getNatsProcessId.</p>
     *
     * @param output a {@link String} object.
     * @return a int.
     */
    private int getNatsProcessId(String output) {
        Pattern pattern = Pattern.compile("\\[\\d+]");
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            String value = matcher.group(0);
            return Integer.parseInt(value.replace("[", "").replace("]", ""));
        }
        return -1;
    }

    /**
     * <p>knownFailureMessages.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    private Set<String> knownFailureMessages() {
        HashSet<String> failureMessages = new HashSet<>();
        failureMessages.add("Error listening on port");
        failureMessages.add("flag provided but not defined");
        return failureMessages;
    }

    private String getSuccessMessage() {
        if (serverConfig.getServerType() == ServerType.NATS_STREAMING)
            return "----------------------------------";
        else
            return "Server is ready";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void stopInternal() {
        synchronized (this) {
            if (!stopped) {
                stopped = true;
                if (!sendKillToProcess()) {
                    tryKillToProcess();
                }
                stopProcess();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cleanupInternal() {

    }


}
