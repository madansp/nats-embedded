package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * <p>NatsVersion class.</p>
 *
 * @author Madan Pokharel
 * @version 1.0.0
 */
public class NatsVersion implements IVersion {
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
    /**
     * Constant <code>V1_4_1</code>
     */
    public static final NatsVersion V1_4_1 = new NatsVersion("v1.4.1");
    /**
     * Constant <code>V1_4_0</code>
     */
    public static final NatsVersion V1_4_0 = new NatsVersion("v1.4.0");

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

}
