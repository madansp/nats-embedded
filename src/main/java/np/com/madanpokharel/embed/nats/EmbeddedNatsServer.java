package np.com.madanpokharel.embed.nats;

import java.util.Objects;


/**
 * <p>EmbeddedNatsServer class.</p>
 *
 * @author Madan Pokharel
 *
 */
public final class EmbeddedNatsServer {
    private EmbeddedNatsConfig config;

    private NatsServerProcess process;
    private NatsServerExecutable executable;

    /**
     * <p>Constructor for EmbeddedNatsServer.</p>
     *
     * @param config a {@link np.com.madanpokharel.embed.nats.EmbeddedNatsConfig} object.
     */
    public EmbeddedNatsServer(EmbeddedNatsConfig config) {
        Objects.requireNonNull(config, "config cannot be null");
        this.config = config;
    }


    /**
     * <p>startServer.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void startServer() throws Exception {
        NatsServerStarter natsServerStarter = new NatsServerStarter(config.getRunTimeConfig());
        executable = natsServerStarter.prepare(config.getServerConfig());
        process = executable.start();
    }


    /**
     * <p>isServerRunning.</p>
     *
     * @return a boolean.
     */
    public boolean isServerRunning() {
        return !Objects.isNull(process) && process.isProcessRunning();
    }


    /**
     * <p>stopServer.</p>
     */
    public void stopServer() {
        process.stop();
        executable.stop();
    }

    /**
     * <p>getRunningPort.</p>
     *
     * @return a int.
     */
    public int getRunningPort() {
        return config.getServerConfig().getPort();
    }

    /**
     * <p>getRunningHost.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRunningHost() {
        return config.getServerConfig().getHost();
    }

    /**
     * <p>getNatsUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNatsUrl() {
        return "nats://" + getRunningHost() + ":" + getRunningPort();
    }

    /**
     * <p>getClusterId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClusterId() {
        return this.config.getServerConfig().getClusterId();
    }

    /**
     * <p>getServerConfig.</p>
     *
     * @return a {@link np.com.madanpokharel.embed.nats.NatsServerConfig} object.
     */
    public NatsServerConfig getServerConfig() {
        return this.config.getServerConfig();
    }

}
