## java 2 proto 2 grpc
*Still work in progress*.

This project is a modification from original project https://github.com/jhrgitgit/java2proto.
Credits belong to the creator of the above mentioned project.
I just made it compatible with Windows, renamed some packages, tried to fix minor bugs, added usage of *protobuf-converter* 
(https://github.com/BAData/protobuf-converter) to transform domain model objects to protobuf messages and viceversa.


#### What this project does:

- Depends on Maven.
- Generates *.proto* files out of Java classes/interfaces (by the moment classes/interfaces existing only in this project).
- Generates gRPC stubs out of *.proto files*.
- Provides two gRPC examples: *Helloworld* and *LoginService*.


#### Usage:

First you need to generate **.proto** files out of your java **classes/interfaces**. Currently only considering classes/interfaces located at 
package **com.harlan.javagrpc.service.contract**.
Next command will generates *.proto* files at **src/main/proto**:
```sh
mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.PackageUtil2
```

Then you can build the project (*mvn compile*) which uses  maven plugin *org.xolstice.maven.plugins:protobuf-maven-plugin* in order to generate 
the protobuf java classes and gRPC stubs for client and server out of your *.proto* files. Generated code is located at *target/generated-sources/protobuf/*.


#### Helloworld and LoginService correct build:

Folder **src/main/proto** contains two commited files named *helloworld.proto* and *LoginService.proto*. Any change on those files are ignored as per 
.gitignore rules. So you can play around with them.

The file *helloworld.proto* is used to generated grpc-java example classes as per https://github.com/grpc/grpc-java/tree/master/examples, 
so you can make some testing running *com.harlan.javagrpc.main.helloworld.HelloWorldClient* and *com.harlan.javagrpc.main.helloworld.HelloWorldServer*.

The file *LoginService.proto* is the one you can generate running *com.harlan.javagrpc.main.PackageUtil2*, and it generates protobuf classes 
and grpc stubs to make some testing running *com.harlan.javagrpc.main.login.LoginClient* and *com.harlan.javagrpc.main.login.LoginServer*.


#### TODO
- Fix weird issue where some fields are defined in parent message definition. Probably due to miss use of HashTreeMap. Seems very related to next fix.
- Fix circular field declarations. See *Request.java* and *Request2.java*. It seems the problem resides on equal field names in some protobuf message 
- Test Enum types with and without fields.
definitions. Maybe using nested messages solves the problem. 
- Add custom Java *Annotations* to classes or fields in order to collect reserved field tags and names for the .proto definition file.
- Support *@java.lang.Deprecated* on classes. It translates to *option deprecated = true;* after message declaration on the .proto file.
- Support *@java.lang.Deprecated* on java fields. It translates to *[deprecated = true];* after field declaration on the .proto file.
- Test building for Java 9 and higher. I had some building errors on generated gRPC stubs due to Java internal relocation of some annotations.


#### License
Licenses corresponds to projects:
- https://github.com/jhrgitgit/java2proto
- https://github.com/BAData/protobuf-converter#license


#### Help tips
- I have some tracked files in a repository which are automatically modified when building the code. 
I don't want to untrack them, I just don't want them to appear as modified and I don't want them to be staged when I git add.
Solution:
	```sh
	git update-index --assume-unchanged file
	```
	To undo and start tracking again:
	```sh
	git update-index --no-assume-unchanged [<file> ...]
	```
