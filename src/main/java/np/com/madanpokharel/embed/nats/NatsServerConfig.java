package np.com.madanpokharel.embed.nats;

import de.flapdoodle.embed.process.config.IExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * <p>NatsServerConfig class.</p>
 *
 * @author Madan Pokharel
 *
 */
public final class NatsServerConfig implements IExecutableProcessConfig {

    private IVersion version;
    private int port;
    private String host;
    private String clusterId;
    private ServerType serverType;
    private Map<String, String> params = new HashMap<>();


    /**
     * <p>Constructor for NatsServerConfig.</p>
     *
     * @param version      a {@link de.flapdoodle.embed.process.distribution.IVersion} object.
     * @param port         a int.
     * @param host         a {@link java.lang.String} object.
     * @param clusterId    a {@link java.lang.String} object.
     * @param serverType   a {@link np.com.madanpokharel.embed.nats.ServerType} object.
     * @param configParams a {@link java.util.Map} object.
     */
    public NatsServerConfig(IVersion version, int port, String host,
                            String clusterId, ServerType serverType, Map<String, String> configParams) {
        this.version = version;
        this.port = port;
        this.host = host;
        this.clusterId = clusterId;
        this.serverType = serverType;
        this.params.putAll(configParams);
    }

    /**
     * <p>Getter for the field <code>port</code>.</p>
     *
     * @return a int.
     */
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IVersion version() {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISupportConfig supportConfig() {
        return new ISupportConfig() {
            @Override
            public String getName() {
                return "nats-streaming-server";
            }

            @Override
            public String getSupportUrl() {
                return "https://github.com/madansp/nats-embedded";
            }

            @Override
            public long maxStopTimeoutMillis() {
                return 5000;
            }

            @Override
            public String messageOnException(Class<?> aClass, Exception e) {
                return e.getMessage();
            }
        };
    }


    /**
     * <p>getConfigList.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getConfigList() {
        List<String> configMap = new ArrayList<>();

        configMap.add("--port");
        configMap.add(Integer.toString(port));

        configMap.add("--addr");
        configMap.add(host);

        if (StringUtils.isNotBlank(clusterId)) {
            configMap.add("-cid");
            configMap.add(clusterId);
        }

        if (!configMap.isEmpty()) {
            params.forEach((k, v) -> {
                configMap.add(k);
                configMap.add(v);
            });
        }

        return configMap;
    }

    /**
     * <p>Getter for the field <code>host</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHost() {
        return host;
    }

    /**
     * <p>Getter for the field <code>clusterId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     * <p>Getter for the field <code>serverType</code>.</p>
     *
     * @return a {@link np.com.madanpokharel.embed.nats.ServerType} object.
     */
    public ServerType getServerType() {
        return serverType;
    }

    public static class Builder {
        private IVersion version;
        private int port;
        private String host;
        private String clusterId;
        private ServerType serverType;
        private Map<String, String> configParams = new HashMap<>();

        public Builder() {
            this.port = 4222;
            this.host = "0.0.0.0";
        }

        public Builder withRandomPort() {
            try {
                this.port = Network.getFreeServerPort();
            } catch (IOException ex) {
                throw new RuntimeException("unbale to allocate random port");
            }
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withClusterId(String clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Builder withServerType(ServerType serverType) {
            this.serverType = serverType;
            return this;
        }

        public Builder withNatsVersion(NatsVersion version) {
            this.version = version;
            return this;
        }

        public Builder withNatsStreamingVersion(NatsStreamingVersion natsStreamingVersion) {
            this.version = natsStreamingVersion;
            return this;
        }

        public Builder withConfigParams(Map<String, String> params) {
            this.configParams.putAll(params);
            return this;
        }

        public Builder withConfigParam(String param, String value) {
            this.configParams.put(param, value);
            return this;
        }

        public NatsServerConfig build() {
            Objects.requireNonNull(serverType, "server type cannot be null");

            if (Objects.isNull(version)) {
                if (serverType == ServerType.NATS) {
                    this.version = NatsVersion.V2_1_0;
                } else {
                    this.version = NatsStreamingVersion.V0_16_2;
                }
            }

            if (serverType == ServerType.NATS) {
                if (StringUtils.isNotBlank(clusterId)) {
                    throw new IllegalArgumentException("cluster id is not applicable for nats server");
                }

                if (!(version instanceof NatsVersion)) {
                    throw new IllegalArgumentException("version do not match server type");
                }
            }

            if (serverType == ServerType.NATS_STREAMING && !(version instanceof NatsStreamingVersion)) {
                throw new IllegalArgumentException("version do not match server type");
            }

            return new NatsServerConfig(version, port, host, clusterId, serverType, configParams);
        }
    }
}
