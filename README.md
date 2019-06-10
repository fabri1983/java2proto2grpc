## java 2 proto 2 grpc

This project is a modification from original projects https://github.com/jhrgitgit/java2proto and https://github.com/lloydsparkes/java-proto-generator.  
Credits belong to the creators of the mentioned projects.  
I just made it compatible with Windows, renamed some packages, fixed some bugs, added usage of *protobuf-converter* 
(https://github.com/BAData/protobuf-converter) with custom modifications to transform domain model objects to protobuf messages and viceversa, 
created LoginService client and server, and more.


#### Features:

- Depends on Maven (uses plugins to generate grpc stubs).
- Java 8 (and minor versions too). See the **TODO** section for Java 9+.
- Generates *.proto* files (syntax v3) out of Java classes/interfaces existing in the classpath and decorated by *@GrpcEnabled*.
- Generates gRPC stubs out of *.proto files*.
- Provides two gRPC examples: *Helloworld* and *LoginService*.
- *java.lang.Enum* is defined as *string* when generating proto file. So when using *@net.badata.protobuf.converter.annotation.ProtoField* 
you need to extend *net.badata.protobuf.converter.type.EnumStringConverter* and set it as *converter* attribute. See **Request** and **Response** examples classes.
- Conversion api between protobuf objects and DTOs or Domain Model Objects, and viceversa.


#### Usage:

First you need to generate **.proto** files out of your java **classes/interfaces** located at your classpath 
and which are decorated with annotation *@GrpcEnabled*.
- **JavaToProto2Main**: generates *.proto* files from a class/package at specific folder:  
	```sh
	mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.JavaToProto2Main -Dexec.args="com.harlan.javagrpc.service.contract src/main/proto"
	```
- JavaToProtoMain: generates *.proto* files from a class/package at specific folder:  
	**Currently work in progress. Messages are being nested and it ends up with lot of repeated messages.**
	```sh
	mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.JavaToProtoMain -Dexec.args="com.harlan.javagrpc.service.contract src/main/proto"
	```

Then you can build the project (*mvn compile* or *Build command* in your IDE) which uses plugin *org.xolstice.maven.plugins:protobuf-maven-plugin* 
in order to generate the protobuf java classes and gRPC stubs for client and server out of your *.proto* files.    
Generated code is located at *target/generated-sources/protobuf/*.


#### Helloworld and LoginService correct build:

Folder **src/main/proto** contains two commited files named *helloworld.proto* and *LoginService.proto*. If you plan to make modificaitons on them you 
can use next commands in order to ignore track any change:
```sh
git update-index --assume-unchanged src/main/proto/helloworld.proto
git update-index --assume-unchanged src/main/proto/LoginService.proto
```

The file *helloworld.proto* is used to generated grpc-java example classes as per https://github.com/grpc/grpc-java/tree/master/examples, 
so you can make some testing running *com.harlan.javagrpc.main.helloworld.HelloWorldClient* and *com.harlan.javagrpc.main.helloworld.HelloWorldServer*.

The file *LoginService.proto* is the one you can generate running *com.harlan.javagrpc.main.JavaToProtoMain2*, and it generates protobuf classes 
and grpc stubs to make some testing running *com.harlan.javagrpc.main.login.LoginClient* and *com.harlan.javagrpc.main.login.LoginServer*.


#### TODO
- Modularize JavaToProto2. Code is written in a very imperative way, and hard to mantain.
- Add converters similar to *net.badata.protobuf.converter.type.DateLongConverter* for fields with type: LocalTime, LocalDate, Date. 
Use *google.protobuf.Timestamp* in the converter implementation.
- Add converters similar to *net.badata.protobuf.converter.type.DateLongConverter* for fields with type: Duration. 
Use *google.protobuf.Duration* in the converter implementation.
- Add custom Java *Annotations* to classes and/or fields in order to collect reserved field tags and names for the .proto definition file.
- Support *@java.lang.Deprecated* on classes. It translates to *option deprecated = true;* after message declaration on the .proto file.
- Support *@java.lang.Deprecated* on java fields. It translates to *[deprecated = true];* after field declaration on the .proto file.
- Java 9+: I had some building errors when generating gRPC stubs due to Java internal relocation of *@javax.annotation.Generated*.
	- See https://github.com/protocolbuffers/protobuf/issues/42.
	- Custom fix: add to your project classpath next dependency: *javax.annotation:javax.annotation-api*.
- Java9+: In Java 8 when gathering parameter name we get Arg0, Arg1, etc. Java9+ might return the real parameter name so probably I need to 
make an adjustment in protobuf message generation. 


#### License
Licenses corresponds to projects:
- https://github.com/jhrgitgit/java2proto
- https://github.com/lloydsparkes/java-proto-generator
- https://github.com/BAData/protobuf-converter#license


#### Useful tips
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
