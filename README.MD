# Aries RSA Example

This is an example application showing how to use Apache [Aries RSA](http://aries.apache.org/modules/rsa.html)
and Gradle.

It uses the [`org.dm.bundle`](https://github.com/TomDmitriev/gradle-bundle-plugin) plugin to
bundlify the jar and [`osgi-run`](https://github.com/renatoathaydes/osgi-run) to build the
OSGi runtime.

It uses the [Protobuffer-TCP](https://github.com/renatoathaydes/protobuf-tcp-rsa-provider) `DistributionProvider`,
so the remote services rely on [Protobuffer](https://developers.google.com/protocol-buffers/)
for serialization.

Service wiring is performed via Declarative services annotations and a XML descriptor for the client to find the remote
service (this is implemented by the [Local Discovery](https://github.com/apache/aries-rsa/tree/master/discovery/local) 
Aries bundle).

## Building and running

To build the server distribution, simply run:

```
./gradlew creOsgi
```

This will create a OSGi environment under `build/server/`.

To build the client:

```
./gradlew creOsgi -Pclient
```

This will create a OSGi environment under `build/client/`.

To run the server on Linux/Mac:

```
bash build/server/run.sh
```

On Windows:

```
build/server/run.bat
```

Similarly, to run the client:

```
bash build/client/run.sh
```

On both server and client, a [OSGiaaS-CLI shell](https://sites.google.com/a/athaydes.com/renato-athaydes/posts/osgiaas-cli-aclitoruncommandswritteninjvmlanguages?pli=1) 
is started, which allows you to inspect the OSGi environment.

For example, run `ps` to see the installed bundles, or `headers 3` to see the configuration of bundle with ID `3`.

The client exports a command called `send-msg` which can be used to send a String to the server.

Example:

```
osgiaas> send-msg hello server
Sending TestInfo message: hello server
Got response back: info {
  testClass: "class java.lang.String"
  testMethod: "hello server"
}
status: SUCCESS
```

## Project structure

There are 3 modules in this project:

### API module

Contains the Protobuffer definitions, i.e. the serializable objects that will be exchanged between client and server.

The Protobuffer definitions can be found in [messages.proto](api/src/main/proto/messages.proto).

This module also defines a very simple service interface, 
[`MessageService`](api/src/main/java/com/athaydes/osgi/api/MessageService.java).

### osgi-server module

The server-side of this application. It simply implements `MessageService` with a class named
[`TestServer`](osgi-server/src/main/java/com/athaydes/osgi/server/TestServer.java).

As you can see in the Java class, the service is exported using the Declarative Services annotation:

```java
@Component(immediate = true, property = {
        "service.exported.interfaces=*",
        "com.athaydes.protobuf.port=5561"
})
public class TestServer implements MessageService
``` 

`service.exported.interfaces=*` marks the service for remote export.
`com.athaydes.protobuf.port=5561` tells the `Protobuffer-TCP` provider which port to use.

### osgi-client module

Client-side module. It contains only a single class, 
[`MessageSender`](osgi-client/src/main/java/com/athaydes/osgi/client/MessageSender.java), which consumes the
`MessageService` service, and locally exports the `org.apache.felix.shell.Command` service so that the CLI
can see the `send-msg` command it implements.

To find the remote `MessageSender` service, this bundle provides a 
[XML descriptor](osgi-client/src/main/resources/OSGI-INF/remote-service/server.xml) which is used by the
[Local Discovery](https://github.com/apache/aries-rsa/tree/master/discovery/local) Aries bundle to wire the service.

## OSGi environment

All bundles required to run this application can be found in the [build.gradle](build.gradle) file, under 
`dependencies` and the `runOsgi` blocks.

Type `./gradlew dep` to see the dependency tree, or, after starting the CLI, type `ps` to see all installed bundles.

> IMPORTANT: Notice that the RSA Topology Manager must have a start level higher than the RSA Core bundle otherwise
  the Topology Manager might fail to see the RSA Admin Service.
