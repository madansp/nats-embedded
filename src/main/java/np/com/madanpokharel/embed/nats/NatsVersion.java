package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.distribution.Version;

/**
 * <p>NatsVersion class.</p>
 *
 * @author Madan Pokharel
 */
public class NatsVersion implements Version {
    /**
     * Constant <code>LATEST</code>
     */
    public static final NatsVersion LATEST = new NatsVersion("v2.1.6");

    /**
     * Constant <code>V2_1_6</code>
     */
    public static final NatsVersion V2_1_6 = new NatsVersion("v2.1.6");

    /**
     * Constant <code>V2_1_4</code>
     */
    public static final NatsVersion V2_1_4 = new NatsVersion("v2.1.4");

    /**
     * Constant <code>V2_1_2</code>
     */
    public static final NatsVersion V2_1_2 = new NatsVersion("v2.1.2");
    /**
     * Constant <code>V2_1_0</code>
     */
    public static final NatsVersion V2_1_0 = new NatsVersion("v2.1.0");
    /**
     * Constant <code>V2_0_4</code>
     */
    public static final NatsVersion V2_0_4 = new NatsVersion("v2.0.4");
    /**
     * Constant <code>V2_0_2</code>
     */
    public static final NatsVersion V2_0_2 = new NatsVersion("v2.0.2");
    /**
     * Constant <code>V2_0_0</code>
     */
    public static final NatsVersion V2_0_0 = new NatsVersion("v2.0.0");


    private final String version;

    /**
     * <p>Constructor for NatsVersion.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public NatsVersion(String version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asInDownloadPath() {
        return version;
    }

    @Override
    public String toString() {
        return version;
    }
}
