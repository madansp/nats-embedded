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

/**
 * <p>ServerType class.</p>
 *
 * @author Madan Pokharel
 * @version 1.0.0
 */
public enum ServerType {
    NATS("nats-server"),
    NATS_STREAMING("nats-streaming-server");

    private final String serverName;

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
