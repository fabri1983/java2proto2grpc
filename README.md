This project is a modification from original project at https://github.com/jhrgitgit/java2proto
Credits belong to the creator of the above mentioned project.

This project generates *.proto* files out of java classes/interfaces, and then by building the project using maven you can generate your gRPC stubs.

Usage:

First you need to create *.proto* files out of your java services interfaces
```sh
mvn exec:exec -Dexec.executable="java" -Dexec.mainClass="com.harlan.javagrpc.PackageUtil"
```
or
```sh
mvn exec:exec -Dexec.executable="java" -Dexec.mainClass="com.harlan.javagrpc.PackageUtil2"
```sh

Then you can build this project which uses maven plugins in order to generate the gRPC stubs for client and server out of your *.proto* files.