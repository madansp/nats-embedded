package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * <p>NatsStreamingVersion class.</p>
 *
 * @author Madan Pokharel
 *
 */
public class NatsStreamingVersion implements IVersion {
    /**
     * Constant <code>LATEST</code>
     */
    public static final NatsStreamingVersion LATEST = new NatsStreamingVersion("v0.16.2");
    /**
     * Constant <code>V0_16_2</code>
     */
    public static final NatsStreamingVersion V0_16_2 = new NatsStreamingVersion("v0.16.2");
    /**
     * Constant <code>V0_16_0</code>
     */
    public static final NatsStreamingVersion V0_16_0 = new NatsStreamingVersion("v0.16.0");
    /**
     * Constant <code>V0_15_1</code>
     */
    public static final NatsStreamingVersion V0_15_1 = new NatsStreamingVersion("v0.15.1");
    /**
     * Constant <code>V0_15_0</code>
     */
    public static final NatsStreamingVersion V0_15_0 = new NatsStreamingVersion("v0.15.0");
    /**
     * Constant <code>V0_14_3</code>
     */
    public static final NatsStreamingVersion V0_14_3 = new NatsStreamingVersion("v0.14.3");

    private final String version;

    /**
     * <p>Constructor for NatsStreamingVersion.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public NatsStreamingVersion(String version) {
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
