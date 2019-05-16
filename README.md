## java 2 proto 2 grpc
This project is a modification from original project https://github.com/jhrgitgit/java2proto.
Credits belong to the creator of the above mentioned project.
I just made it compatible with Windows, renamed some packages, tried to fix minor bugs. *Still work in progress*.

This project generates *.proto* files out of java classes/interfaces, and then by building the project using maven you can generate your gRPC stubs.


#### Usage:

First you need to create **.proto** files out of your java **classes/interfaces**. Currently only consider classes/interfaces located at package **com.harlan.javagrpc.service**.
```sh
mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.PackageUtil2
```

Then you can build this project which uses maven plugins in order to generate the gRPC stubs for client and server out of your *.proto* files.
Generated *.proto* files are located at **src/main/proto** which then are used by maven plugin *org.xolstice.maven.plugins:protobuf-maven-plugin* 
to generate **gRPC** stubs at *target/generated-sources/protobuf/*.


#### HelloWorld correct compilation:

Folder **src/main/proto** contains one commited file named helloworld.proto. 
This file is used to generated grpc-java example classes as per https://github.com/grpc/grpc-java/tree/master/examples, 
so you can make some test running *com.harlan.javagrpc.main.HelloWorldClient* and *com.harlan.javagrpc.main.HelloWorldServer*.


#### TODO
- Test Enum types with and without fields.
- Add gRPC example using LoginService gRPC stubs. 
- Fix circular field declarations. See *Request.java* and *Request2.java*. It seems the problem resides on equal field names in some protobuf message definitions. Maybe using nested messages solves the problem. 
- Add custom Java *Annotations* to classes or fields in order to collect reserved field tags and names for the .proto definition file.
- Support *@java.lang.Deprecated* on classes. It translates to *option deprecated = true;* after message declaration on the .proto file.
- Support *@java.lang.Deprecated* on java fields. It translates to *[deprecated = true];* after field declaration on the .proto file.
- Test building for Java 9 and higher. I had some building errors on generated gRPC stubs due to Java internal relocation of some annotations.

#### License
Licenses corresponds to projects:
- https://github.com/jhrgitgit/java2proto
- https://github.com/BAData/protobuf-converter#license