[![Build Status](https://travis-ci.com/madansp/nats-embedded.svg?branch=master)](https://travis-ci.com/madansp/nats-embedded)
### Embedded Nats and Streaming Sever

Embedded Nats and Nats Streaming server for integration tests.

## Usage
Add the following dependency to project

```xml
<dependency>
   <groupId>np.com.madanpokharel.embed</groupId>
   <artifactId>nats-embedded</artifactId>
   <version>0.1.0</version>
   <scope>test</scope>
 </dependency>
```

#### Example

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
                                .withConfigParam("--store","FILE") //custom config parameter
                                .withConfigParam("--dir","target/nats") //custom config parameter
                                .withNatsStreamingVersion(NatsStreamingVersion.V0_16_2)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsStreamingServer = new EmbeddedNatsServer(config);
        natsStreamingServer.startServer();
```
### Dependency
This library using component from  [flapdoodle-oss](https://github.com/flapdoodle-oss)
