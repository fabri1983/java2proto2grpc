## java 2 proto 2 grpc

This project is a modification from original projects https://github.com/jhrgitgit/java2proto and https://github.com/lloydsparkes/java-proto-generator.
Credits belong to the creator of the mentioned projects.
I just made it compatible with Windows, renamed some packages, tried to fix minor bugs, added usage of *protobuf-converter* 
(https://github.com/BAData/protobuf-converter) to transform domain model objects to protobuf messages and viceversa, and created LoginService client and server.


#### What this project does:

- Depends on Maven.
- Generates *.proto* files out of Java classes/interfaces existing in the classpath.
- Generates gRPC stubs out of *.proto files*.
- Provides two gRPC examples: *Helloworld* and *LoginService*.


#### Usage:

First you need to generate **.proto** files out of your java **classes/interfaces** located at your classpath.
- Use PackageUtil2: generates *.proto* files from *com.harlan.javagrpc.service.contract* at **src/main/proto**:
	```sh
	mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.PackageUtil2
	```
- Use JavaToProtoMain: generates *.proto* files from a class/package at specific folder:
	**Currently not working. I'm updating the code to genertae syntax version 3.**
	```sh
	mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.JavaToProtoMain com.harlan.javagrpc.service.contract src/main/proto
	```

Then you can build the project (*mvn compile*) which uses  maven plugin *org.xolstice.maven.plugins:protobuf-maven-plugin* in order to generate 
the protobuf java classes and gRPC stubs for client and server out of your *.proto* files. Generated code is located at *target/generated-sources/protobuf/*.


#### Helloworld and LoginService correct build:

Folder **src/main/proto** contains two commited files named *helloworld.proto* and *LoginService.proto*. If you plan to made modificaitons on them you 
can use next commands in order to ignore track any change:
```sh
git update-index --assume-unchanged src/main/proto/helloworld.proto
git update-index --assume-unchanged src/main/proto/LoginService.proto
```

The file *helloworld.proto* is used to generated grpc-java example classes as per https://github.com/grpc/grpc-java/tree/master/examples, 
so you can make some testing running *com.harlan.javagrpc.main.helloworld.HelloWorldClient* and *com.harlan.javagrpc.main.helloworld.HelloWorldServer*.

The file *LoginService.proto* is the one you can generate running *com.harlan.javagrpc.main.PackageUtil2*, and it generates protobuf classes 
and grpc stubs to make some testing running *com.harlan.javagrpc.main.login.LoginClient* and *com.harlan.javagrpc.main.login.LoginServer*.


#### TODO
- Fix weird issue where some fields are defined in parent message definition instead of current processing message.
Probably due to missuse of HashTreeMap. Seems very related to next fix.
- Fix circular field declarations. See *Request.java* and *Request2.java*. It seems the problem resides on equal field names in some protobuf message 
definitions. Maybe using nested messages solves the problem. 
- Test Enum types with and without fields.
- Add custom Java *Annotations* to classes or fields in order to collect reserved field tags and names for the .proto definition file.
- Support *@java.lang.Deprecated* on classes. It translates to *option deprecated = true;* after message declaration on the .proto file.
- Support *@java.lang.Deprecated* on java fields. It translates to *[deprecated = true];* after field declaration on the .proto file.
- Test building for Java 9 and higher. I had some building errors when generating gRPC stubs due to Java internal relocation of *@javax.annotation.Generated*.
See https://github.com/protocolbuffers/protobuf/issues/42.


#### License
Licenses corresponds to projects:
- https://github.com/jhrgitgit/java2proto
- https://github.com/lloydsparkes/java-proto-generator
- https://github.com/BAData/protobuf-converter#license


#### Useful tips
- I have some tracked files in a repository which are automatically modified when building the code. 
I don't want to untrack them, I just don't want them to appear as modified and I don't want them to be staged when I git add.
Solution:
	```sh
	git update-index --assume-unchanged <file>
	```
	To undo and start tracking again:
	```sh
	git update-index --no-assume-unchanged [<file> ...]
	```
