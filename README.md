## java 2 proto 2 grpc
This project is a modification from original project at https://github.com/jhrgitgit/java2proto.
Credits belong to the creator of the above mentioned project.
I just made it compatible with windows and renamed some packages. 

This project generates *.proto* files out of java classes/interfaces, and then by building the project using maven you can generate your gRPC stubs.

**Usage:**

First you need to create **.proto** files out of your **java classes/interfaces**.
```sh
mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.PackageUtil
```
or
```sh
mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.PackageUtil2
```

Then you can build this project which uses maven plugins in order to generate the gRPC stubs for client and server out of your *.proto* files.
Generated *.proto* files are located at **src/main/proto** which then are used by maven plugin *org.xolstice.maven.plugins:protobuf-maven-plugin* 
to generate **gRPC** stubs at *target/generated-sources/protobuf/*.


** HelloWorld correct compilation**
Folder **src/main/proto** contains one commited file named helloworld.proto. 
This file is used to generated grpc-java example classes as per https://github.com/grpc/grpc-java/tree/master/examples, 
so you can make some test running *com.harlan.javagrpc.main.HelloWorldClient* and *com.harlan.javagrpc.main.HelloWorldServer*.
