![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/madansp/nats-embedded?style=for-the-badge&logo=appveyor)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=for-the-badge&logo=appveyor)](https://opensource.org/licenses/Apache-2.0)
![Maven Central](https://img.shields.io/maven-central/v/np.com.madanpokharel.embed/nats-embedded?style=for-the-badge&logo=appveyor)
### Embedded Nats and Streaming Sever

Embedded Nats and Nats Streaming server for integration tests.
Tested with Windows, Mac OSX and Linux

## Usage
Add the following dependency to project     
For Maven:

```xml
<dependency>
   <groupId>np.com.madanpokharel.embed</groupId>
   <artifactId>nats-embedded</artifactId>
   <version>2.1.0</version>
   <scope>test</scope>
 </dependency>
```
For Gradle(Groovy Style):
``` groovy
testCompile group: 'np.com.madanpokharel.embed', name: 'nats-embedded', version: '2.1.0'
```

For Gradle
```kotlin
implementation("np.com.madanpokharel.embed:nats-embedded:2.1.0")
```
For Bazel
```
maven_jar(
    name = "nats-embedded",
    artifact = "np.com.madanpokharel.embed:nats-embedded:2.1.0",
    sha1 = "0235584434668a4dfbb9b7a0211cf3c9642e99bd",
)
```

#### Example

Start nats server with defined port 7656.

```java
 EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS) //Can be omitted, default is Nats
                                .withPort(7656)
                                .withNatsVersion(NatsVersion.LATEST)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
        //start the server
        natsServer.startServer();

//get running port
System.out.pritnln(natsServer.getRunningPort())

//stop the server
natsServer.stopServer();
```

Start with default settings. This will download latest version and start server on random port. 

```java
EmbeddedNatsConfig config =  EmbeddedNatsConfig.defaultNatsServerConfig();
EmbeddedNatsServer natsServer = new EmbeddedNatsServer(config);
natsServer.startServer();

//get running port
System.out.println(natsServer.getRunningPort())

natsServer.stopServer();

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
                                .withNatsStreamingVersion(NatsStreamingVersion.LATEST)
                                .build()
                )
                .build();
        EmbeddedNatsServer natsStreamingServer = new EmbeddedNatsServer(config);
        natsStreamingServer.startServer();
```

Customizing the download location and extraction path

```java
 EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig(
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS)
                                .withPort(port)
                                .withNatsVersion(NatsVersion.LATEST)
                                .build()
                )
                .withArtifactStorePath(".custom-download-dir") //custom download location
                .withExtractDirectory(".custom-extract-dir") // custom location for extraction of zip file
                .build();
```

Example of Nats server with full configurations

```java
EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig( //configuration for nats server configuration
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS) // server type either nats or nats streaming
                                .withPort(2345) // port to run. can withRandomPort() if want to run on random port
                                .withNatsVersion(NatsVersion.V2_1_0) // version to use
                                .withHost("127.0.0.1") // host ip
                                .withConfigParam("--store","FILE") // single config parameter 
                                .withConfigParams(Collections.singletonMap("--dir","target/nats")) // map of config parameters
                                .build()
                )
                .withArtifactStorePath(".custom-download-dir") // location for saving downloaded file
                .withExtractDirectory(".custom-extract-dir") // location for extracting downloaded zip file
                .build();
```
Example of Nats Streaming server with full configurations
```java
EmbeddedNatsConfig config = new EmbeddedNatsConfig.Builder()
                .withNatsServerConfig( //configuration for nats server configuration
                        new NatsServerConfig.Builder()
                                .withServerType(ServerType.NATS_STREAMING) // server type either nats or nats streaming
                                .withPort(3445) // port to run. can withRandomPort() if want to run on random port
                                .withNatsStreamingVersion(NatsStreamingVersion.V0_16_2)  // version to use
                                .withClusterId("clusterId") //cluster Id
                                .withHost("127.0.0.1") // host ip
                                .withConfigParam("--store","FILE") // single config parameter
                                .withConfigParams(Collections.singletonMap("--dir","target/nats")) // map of config parameters
                                .build()
                )
                .withArtifactStorePath(".custom-download-dir") // location for saving downloaded file
                .withExtractDirectory(".custom-extract-dir") // location for extracting downloaded zip file
                .build();

```
Using custom nats configuration file
```java
new NatsServerConfig.Builder()
        .withConfigurationFile("custom-config-file.conf")
        .build()
```
Enabling JetStream
```java
 new NatsServerConfig.Builder()
        .withEnableJetStream() //without store location
        .build() 
        
new NatsServerConfig.Builder()
        .withEnableJetStream("target") //with store location
        .build()
```
### Dependency
This library using component from  [flapdoodle-oss](https://github.com/flapdoodle-oss)
