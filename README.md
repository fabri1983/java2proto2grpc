# java 2 proto 2 grpc

[![Build Status](https://app.travis-ci.com/fabri1983/java2proto2grpc.svg?branch=master)](https://app.travis-ci.com/fabri1983/java2proto2grpc?branch=master)
&nbsp;&nbsp;&nbsp;&nbsp;
[![Coverage Status](https://coveralls.io/repos/github/fabri1983/java2proto2grpc/badge.svg)](https://coveralls.io/github/fabri1983/java2proto2grpc?branch=master)
&nbsp;&nbsp;&nbsp;&nbsp;
[![Code Climate](https://codeclimate.com/github/fabri1983/java2proto2grpc/badges/gpa.svg)](https://codeclimate.com/github/fabri1983/java2proto2grpc)
&nbsp;&nbsp;&nbsp;&nbsp;
[![Libraries.io for GitHub](https://badgen.net/badge/libraries.io/fabri1983/blue)](https://libraries.io/github/fabri1983/java2proto2grpc)


This project is based on two projects: [java2proto](https://github.com/jhrgitgit/java2proto) 
and [java-proto-generator](https://github.com/lloydsparkes/java-proto-generator), which are no longer maintained by their authors.  
I renamed some packages, fixed some bugs, made use of better design patterns, added LoginService Client and Server examples and tests, 
added TLS usage, extended and fixed api [protobuf-converter](https://github.com/BAData/protobuf-converter) with custom modifications to transform domain 
model objects to protobuf messages and viceversa, added Service Discovery capability, and many more.


Features and Info:
---
- Depends on **Maven** (uses plugins to generate grpc stubs). If you don't have Maven installed then use provided `mvnw`.
- **Java 8+**. 
	- note the use of dependency `javax.annotation:javax.annotation-api` which solves the issue on generated grpc stubs due to Java internal 
	relocation of `@javax.annotation.Generated` on newer java versions.
- Java 6, 7: requires some changes since the code uses *java.time* package and minor usages of `java.util.stream`. Also some dependencies may break.
- Generates **.proto** files (**IDL syntax v3**) out of Java classes/interfaces existing in the classpath and decorated by `@GrpcEnabled`.
- Use of java compiler `-parameter` option to expose parameters name in signature definition, so we can get the real parameter name and 
so improve the `.proto` file readablity.
- Generates **gRPC stubs** out of `.proto` files.
- Skips generation of protobuf message or inner fields by decorating a class with `@ProtobufSkipFields`. This is particularly usefull when you 
have a class hierarchy and you want to skip one or several of them.
- Conversion api between protobuf objects and DTOs and/or Domain Model Objects, and viceversa:
	- Fixed and extended version of api *protobuf-converter* from [BAData](https://github.com/BAData/protobuf-converter "protobuf-converter").
	- `java.lang.Enum` is defined as `String` when generating proto file. 
	So when using `@org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField` you need to extend `org.fabri1983.javagrpc.protobuf.converter.type.EnumStringConverter` 
	and set it as *converter* attribute. See `Request` and `Response` example classes.
- Provides two gRPC examples: *GreeterService* and *LoginService*.
- Provides non secured and TLS-secured grpc server and client.
- Provides blocking, asynchronous, and futurable grpc calls.
- Provides **Resilience4j's Bulkhead** client interceptor to limit number of concurrent calls.
- Provides a `GrpcManagedChannelServiceDiscovery` with a **Consul Service Discovery** client and **Load Balancer** capability, 
from [grpc-java-load-balancer-using-consul](https://github.com/mykidong/grpc-java-load-balancer-using-consul). See tests.


Usage:
---
First you need to generate `.proto` files out of your java `classes/interfaces` located at your classpath 
and which are decorated with annotation `@GrpcEnabled`.  
You can skip generation of protobuf messages or inner fields decorating a class with `@ProtobufSkipFields`.
- `JavaToProtoNewMain`: generates `.proto` files (**IDL syntax v3**) from a class/package at specific folder:  
	```sh
	mvn compile
	mvn exec:java -Dexec.mainClass=org.fabri1983.javagrpc.main.converter.JavaToProtoNewMain -Dexec.args="org.fabri1983.javagrpc.service.contract src/main/proto"
	```
- JavaToProtoMain: generates `.proto` files (**IDL syntax v3**) from a class/package at specific folder:  
	**Currently work in progress. Messages are being nested and it ends up with lot of repeated messages.**
	```sh
	mvn compile
	mvn exec:java -Dexec.mainClass=org.fabri1983.javagrpc.main.converter.JavaToProtoMain -Dexec.args="org.fabri1983.javagrpc.service.contract src/main/proto"
	```

Then you build the project (`mvn compile` or *Build command* in your IDE) which triggers plugin `org.xolstice.maven.plugins:protobuf-maven-plugin` 
in order to generate the protobuf java classes and gRPC stubs for client and server out of your `.proto` files.    
Generated code is located at `target/generated-sources/protobuf/` and `target/generated-test-sources/protobuf/`.


GreeterService and LoginService examples:
---
Folder `src/main/proto` contains two commited files named `Greeter.proto` and `LoginService.proto`. If you plan to make modifications 
on them you can use next commands in order to ignore track any change:
```sh
git update-index --assume-unchanged src/main/proto/Greeter.proto
git update-index --assume-unchanged src/main/proto/LoginService.proto
```

The file `Greeter.proto` is used to generated grpc-java example classes as per [grpc-java examples](https://github.com/grpc/grpc-java/tree/master/examples), 
so you can make some testing running `org.fabri1983.javagrpc.main.greeter.GreeterClientMain` 
and `org.fabri1983.javagrpc.main.greeter.GreeterServerMain`.

The file `LoginService.proto` is the one you can generate running `org.fabri1983.javagrpc.main.converter.JavaToProtoMainNew`, and it generates 
protobuf classes and grpc stubs to make some testing running `org.fabri1983.javagrpc.main.login.LoginClientMain` 
and `org.fabri1983.javagrpc.main.login.LoginServerMain`.


Run Tests with Consul
---
**Consul** is a tool for *service discovery* with load balancing and health check capabilities.  
Consul is distributed, highly available, and extremely scalable. Visit https://github.com/hashicorp/consul.  
  
JUnit `LoginServiceGrpcClientConsulServiceDiscoveryTest` runs a **LoginService gRPC** test with multiple stub calls using a 
`ManagedChannel` which connects to a *Consul* server (local or external, see below).  
JUnit `GreeterServiceGrpcClientLoadBalancerTest` runs a **Greeter gRPC** test with multiple stub calls using a custom gRPC Load Balancer 
on client side using *static gRPC nodes* and also *Consul service discovery*.  

Consul can be obtained as a **stand alone app** or as a **docker image**:  
- stand alone app: https://www.consul.io/downloads.html  
- docker image: 
	- `docker image pull consul`
	- See *Useful Tips* section from this README file for instructions on container execution.

You need to setup the current consul ip address in order to let the test `LoginServiceGrpcClientConsulServiceDiscoveryTest` run correctly:
- If you are using docker in *Windows* with **Docker Tool Box** then get your docker machine ip with:
	```sh
	docker-machine ip default
	192.168.99.100
	```
	and put that ip in the file **src/test/resources/service-discovery.properties**.
- Another way you can get the consul app ip:
	```sh
	docker inspect -f "{{ .NetworkSettings.IPAddress }}" consul
	```
	and put that ip in the file **src/test/resources/service-discovery-test.properties**.
- If running consul as a standalone app then then get your machines's local IP.
	In this case put 127.0.0.1 (if local) or external IP (if in another server) in the file **src/test/resources/service-discovery-test.properties**.


OWASP Dependency Checker
---
Run next command to check if any dependency has a security risk according the Maven plugin *dependency-checker* from **OWASP**:  
```sh
mvn verify -P securitycheck
```


TODO
---
- Add test resulting with a failure BulheadFullException to ensure expected behaviour.
- Expose Resilience4j's metrics as an http endpoint.
- Modularize JavaToProtoNew. Code is written in a very imperative way, and hard to mantain.
- Add converters similar to `org.fabri1983.javagrpc.protobuf.converter.type.XxxConverter` for fields with types: Duration. 
See [this](https://github.com/google/qrisp/blob/master/google/protobuf/java/util/src/main/java/com/google/protobuf/util/TimeUtil.java).
- Replace custom protobuf-converter solution by [MapStruct](http://mapstruct.org/). It's faster and reflection free.
- Add custom Java *Annotations* to classes/interfaces and/or fields in order to collect reserved field tags and names for the .proto IDL file.
- Support `@java.lang.Deprecated` on classes/interfaces. It translates to *option deprecated = true;* after message declaration on the .proto IDL file.
- Support `@java.lang.Deprecated` on java fields. It translates to *[deprecated = true];* after field declaration on the .proto file.


License
---
Licenses correspond to next projects (currently discontinued) as I partially took and modify/improve third party source code:
- https://github.com/jhrgitgit/java2proto
- https://github.com/lloydsparkes/java-proto-generator
- https://github.com/BAData/protobuf-converter#license
- https://github.com/mykidong/grpc-java-load-balancer-using-consul


Useful tips
---
- I have some tracked files which potentially can be modified.  
I don't want to untrack them, I just don't want them to appear as modified and I don't want them to be staged when I git add.  
Solution:
  ```sh
  git update-index --assume-unchanged <file>
  ```
  To undo and start tracking again:
  ```sh
  git update-index --no-assume-unchanged [<file> ...]
  ```

- Running Consul in Docker:  
See this [link](https://docs.docker.com/samples/library/consul/#running-consul-for-development)
  - download docker *Consul* image if not already:  
    ```sh
    docker image pull consul
    ```
  - run *Consul* on *Windows* with *Docker Tool Box*:  
    ```sh
    Development mode:
    docker container run -d -p 8500:8500 -p 172.17.0.1:53:8600/udp -p 8400:8400 -p 8300:8300 --name=consul-dev -e CONSUL_BIND_INTERFACE=eth0 consul
    or
    Agent in Client mode:
    docker container run -d -p 8500:8500 -p 172.17.0.1:53:8600/udp -p 8400:8400 -p 8300:8300 --name=consul-agent consul agent -server -bootstrap -ui -node=docker-1 -client=0.0.0.0 -data-dir=/tmp/node
    ```
  - Within Consul:  
    - port 8300 is used by Consul servers to handle incoming requests from other agents (TCP only).
    - port 8400 is used for Client requests.
    - port 8500 is used for HTTP Api.
    - port 8600 is used for answer DNS queries. By using *-p* option, we are exposing these ports to the host machine.
  - *-client=0.0.0.0* binds to all interfaces (docker network and host network).
  - 172.17.0.1 is the Docker bridge IP address. We are remapping Consul Container’s port 8600 to host machine’s Docker bridge port 53 so that Containers on that host can use Consul for DNS.
  - *-bootstrap* means consul runs in a standalone mode.

- Considerations on *Windows Docker Tool Box*:  
If by any reason you are in a situation in which your app needs to route requests made to Docker's internal IP 172.17.x.x to the exposed IP 192.168.99.100 then:
  - add an entry in the Windows routing table:  
  ```sh
  Open a privileged console
  route add 172.17.0.0 mask 255.255.0.0 192.168.99.100 -p
  Then you can remove that entry with:
  route delete 172.17.0.0
  ```

- Change *Windows Docker Tool Box* machine ip:  
See gist https://gist.github.com/fabri1983/ff900cba76d5daf38ce4506665046c7a. 

- Change *Windows Docker Tool Box* to **Experimental** mode:
  - Open VirtualBox and open the terminal GUI for the default image.
  - Edit/Create `daemon.json` file:
  ```sh
  sudo vi /etc/docker/dameon.json
  {
   "experimental": true
  }
  ```
  - Edit/Create `config.json` file:
  ```sh
  vi ~/.docker/config.json
  {
   "experimental": "enabled"
  }
  ```
  - Deatach from the terminal GUI.
  - Go back to the cmd window:
  ```sh
  docker-machine stop default
  docker-machine start default
  docker info
```

- Install buildx plugin into *Windows Docker Tool Box*:
  - Windows:
    - Download last version (currently 0.4.2) here: https://github.com/docker/buildx/releases/
    - Rename it to `buildx`
    - Move it into `C:\Program Files\Docker Toolbox\`
    - Open a cmd window and type: `buildx version`
  - VirtualBox:
    - Open VirtualBox and open the terminal GUI for the default image.
    - Type:
    ```sh
    export DOCKER_BUILDKIT=1
    docker build --platform=local -o . git://github.com/docker/buildx
    mkdir -p ~/.docker/cli-plugins
    mv buildx ~/.docker/cli-plugins/docker-buildx
    ```
    or
    ```sh
    wget https://github.com/docker/buildx/releases/download/v0.4.2/buildx-v0.4.2.linux-amd64
    chmod a+x buildx-v0.4.2.linux-amd64
    mkdir -p ~/.docker/cli-plugins
    mv buildx-v0.4.2.linux-amd64 ~/.docker/cli-plugins/docker-buildx
    ```
    - You can only use docker buildx under the terminal GUI, not available on Windows:
    ```sh
    docker buildx version
    ```
