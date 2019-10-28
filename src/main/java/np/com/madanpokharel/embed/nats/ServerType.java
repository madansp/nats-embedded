package np.com.madanpokharel.embed.nats;

/**
 * <p>ServerType class.</p>
 *
 * @author Madan Pokharel
 * @version 1.0.0
 */
public enum ServerType {
    NATS("nats-server"),
    NATS_STREAMING("nats-streaming-server");

    private String serverName;

    /**
     * <p>Constructor for ServerType.</p>
     *
     * @param serverName a {@link java.lang.String} object.
     */
    ServerType(String serverName) {
        this.serverName = serverName;
    }

    /**
     * <p>Getter for the field <code>serverName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getServerName() {
        return serverName;
    }
}
