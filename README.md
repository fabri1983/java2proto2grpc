# java 2 proto 2 grpc

[![Build Status](https://travis-ci.org/fabri1983/java2proto2grpc.svg?branch=master)](https://travis-ci.org/fabri1983/java2proto2grpc?branch=master)
&nbsp;&nbsp;&nbsp;&nbsp;
[![Coverage Status](https://coveralls.io/repos/github/fabri1983/java2proto2grpc/badge.svg)](https://coveralls.io/github/fabri1983/java2proto2grpc?branch=master)
&nbsp;&nbsp;&nbsp;&nbsp;
[![Code Climate](https://codeclimate.com/github/fabri1983/java2proto2grpc/badges/gpa.svg)](https://codeclimate.com/github/fabri1983/java2proto2grpc)
&nbsp;&nbsp;&nbsp;&nbsp;
[![Libraries.io for GitHub](https://badgen.net/badge/libraries.io/fabri1983/blue)](https://libraries.io/github/fabri1983/java2proto2grpc)


This project is a modification from original projects https://github.com/jhrgitgit/java2proto and https://github.com/lloydsparkes/java-proto-generator.  
Credits belong to the creators of the mentioned projects.  
I just renamed some packages, fixed some bugs, add LoginService client and server examples and tests, extended and fixed api 
[protobuf-converter](https://github.com/BAData/protobuf-converter "protobuf-converter") with custom modifications to transform domain 
model objects to protobuf messages and viceversa, and more.


Features:
---
- Depends on Maven (uses plugins to generate grpc stubs).
- Java 8+. 
	- note the use of dependency *javax.annotation:javax.annotation-api* which solves the issue on generated grpc stubs due to Java internal relocation of *@javax.annotation.Generated* on newer java versions.
- Java 6, 7: requires some changes since the code uses *java.time* package.
- Generates *.proto* files (**IDL syntax v3**) out of Java classes/interfaces existing in the classpath and decorated by *@GrpcEnabled*.
- Generates gRPC stubs out of *.proto files*.
- Conversion api between protobuf objects and DTOs or Domain Model Objects, and viceversa:
	- Fixed and extended version of api *protobuf-converter* from [BAData](https://github.com/BAData/protobuf-converter "protobuf-converter").
	- *java.lang.Enum* is defined as *string* when generating proto file. 
	So when using *@net.badata.protobuf.converter.annotation.ProtoField* you need to extend *net.badata.protobuf.converter.type.EnumStringConverter* 
	and set it as *converter* attribute. See **Request** and **Response** example classes.
- Provides two gRPC examples: *Helloworld* and *LoginService*.
- Provides non secured and TLS-secured grpc server and client.
- Use async grpc calls by *ListenableFuture*.


Usage:
---
First you need to generate **.proto** files out of your java **classes/interfaces** located at your classpath 
and which are decorated with annotation *@GrpcEnabled*.
- **JavaToProtoNewMain**: generates *.proto* files (**IDL syntax v3**) from a class/package at specific folder:  
	```sh
	mvn compile
	mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.converter.JavaToProtoNewMain -Dexec.args="com.harlan.javagrpc.service.contract src/main/proto"
	```
- JavaToProtoMain: generates *.proto* files (**IDL syntax v3**) from a class/package at specific folder:  
	**Currently work in progress. Messages are being nested and it ends up with lot of repeated messages.**
	```sh
	mvn compile
	mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.converter.JavaToProtoMain -Dexec.args="com.harlan.javagrpc.service.contract src/main/proto"
	```

**Then you build the project** (*mvn compile* or *Build command* in your IDE) which triggers plugin *org.xolstice.maven.plugins:protobuf-maven-plugin* 
in order to generate the protobuf java classes and gRPC stubs for client and server out of your *.proto* files.    
Generated code is located at *target/generated-sources/protobuf/* and *target/generated-test-sources/protobuf/*.


Helloworld and LoginService examples:
---
Folder **src/main/proto** contains two commited files named *helloworld.proto* and *LoginService.proto*. If you plan to make modificaitons on them you 
can use next commands in order to ignore track any change:
```sh
git update-index --assume-unchanged src/main/proto/helloworld.proto
git update-index --assume-unchanged src/main/proto/LoginService.proto
```

The file *helloworld.proto* is used to generated grpc-java example classes as per https://github.com/grpc/grpc-java/tree/master/examples, 
so you can make some testing running *com.harlan.javagrpc.main.helloworld.HelloWorldClientMain* and *com.harlan.javagrpc.main.helloworld.HelloWorldServerMain*.

The file *LoginService.proto* is the one you can generate running *com.harlan.javagrpc.main.converter.JavaToProtoMainNew*, and it generates protobuf classes 
and grpc stubs to make some testing running *com.harlan.javagrpc.main.login.LoginClientMain* and *com.harlan.javagrpc.main.login.LoginServerMain*.


TODO
---
- Modularize JavaToProtoNew. Code is written in a very imperative way, and hard to mantain.
- Add converters similar to *net.badata.protobuf.converter.type.DateLongConverter* for fields with types: LocalTime, LocalDate. 
Use *google.protobuf.Timestamp* in the converter implementation.
- Add converters similar to *net.badata.protobuf.converter.type.DateLongConverter* for fields with types: Duration. 
Use *google.protobuf.Duration* in the converter implementation.
- Add custom Java *Annotations* to classes and/or fields in order to collect reserved field tags and names for the .proto definition file.
- Support *@java.lang.Deprecated* on classes. It translates to *option deprecated = true;* after message declaration on the .proto file.
- Support *@java.lang.Deprecated* on java fields. It translates to *[deprecated = true];* after field declaration on the .proto file.


License
---
Licenses corresponds to projects:
- https://github.com/jhrgitgit/java2proto
- https://github.com/lloydsparkes/java-proto-generator
- https://github.com/BAData/protobuf-converter#license


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
