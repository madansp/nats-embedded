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

import de.flapdoodle.embed.process.distribution.Version;

/**
 * <p>NatsStreamingVersion class.</p>
 *
 * @author Madan Pokharel
 */
public class NatsStreamingVersion implements Version {
    /**
     * Constant <code>LATEST</code>
     */
    public static final NatsStreamingVersion LATEST = new NatsStreamingVersion("v0.17.0");

    /**
     * Constant <code>V0_17_0</code>
     */
    public static final NatsStreamingVersion V0_17_0 = new NatsStreamingVersion("v0.17.0");

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
