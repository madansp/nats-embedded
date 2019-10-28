[![Build Status](https://travis-ci.org/madansp/nats-embedded.svg?branch=master)](https://travis-ci.org/madansp/nats-embedded)
### Embedded Nats and Streaming Sever

Embedded Nats and Nats Streaming server for integration tests.

## Usage
Publishing to maven central is in progress. Clone the source and build locally.


### Example

Start nats server with defined port 7656.

```java
 EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS)
                                .withPort(7656)
                                .withNatsVersion(NatsVersion.V2_1_0)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
        natsServer.startServer();

//get running port
System.out.pritnln(natsServer.getRunningPort())

natsStreamingServer.stopServer();
```

Start with default settings. This will download latest version and start server on random port. 

```java
EmbeddedNatsConfig config =  EmbeddedNatsConfig.defaultNatsServerConfig();
EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
natsServer.startServer();

//get running port
System.out.pritnln(natsServer.getRunningPort())

natsStreamingServer.stopServer();

```
Start with custom config params

```java
EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS_STREAMING)
                                .withClusterId(clusterId)
                                .withRandomPort()
                                .withConfigParam("--store","FILE")
                                .withConfigParam("--dir","target/nats")
                                .withNatsStreamingVersion(NatsStreamingVersion.V0_16_2)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsStreamingServer = new EmbeddedNatsServer(config);
        natsStreamingServer.startServer();
```
### Dependency
This library is based on [flapdoodle-oss embed process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)
