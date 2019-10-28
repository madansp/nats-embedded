package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
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
 * @version 1.0.0
 */
final class NatsServerProcess extends AbstractProcess<NatsServerConfig, NatsServerExecutable, NatsServerProcess> {

    private boolean stopped;
    private NatsServerConfig serverConfig;

    NatsServerProcess(Distribution distribution, NatsServerConfig config, IRuntimeConfig runtimeConfig,
                      NatsServerExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getCommandLine(Distribution distribution, NatsServerConfig config, IExtractedFileSet files) {
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
    protected void onAfterProcessStart(ProcessControl process, IRuntimeConfig runtimeConfig) throws IOException {
        ProcessOutput outputConfig = runtimeConfig.getProcessOutput();

        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(getSuccessMessage(),
                knownFailureMessages(), StreamToLineProcessor.wrap(outputConfig.getError()));

        Processors.connect(process.getError(), logWatch);

        logWatch.waitForResult(5000);

        if (!logWatch.isInitWithSuccess()) {
            throw new IOException("could not start nats proceess");
        }

        setProcessId(getNatsdProcessId(logWatch.getOutput()));

    }

    /**
     * <p>getNatsdProcessId.</p>
     *
     * @param output a {@link String} object.
     * @return a int.
     */
    private int getNatsdProcessId(String output) {
        Pattern pattern = Pattern.compile("\\[\\d+]", Pattern.MULTILINE);
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
            return "Streaming Server is ready";
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
                    System.out.println("trying to kill process");
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
