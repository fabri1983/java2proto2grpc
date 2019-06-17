package com.harlan.javagrpc.main.helloworld;

import com.halran.javagrpc.grpc.artifact.GrpcServerStarter;
import com.halran.javagrpc.grpc.artifact.IGrpcServerStarter;
import com.harlan.javagrpc.service.GreeterServiceGrpcImpl;

import java.io.IOException;

public class HelloWorldServerMain {

	public static void main(String[] args) throws IOException {
		int port = 50051;
		boolean withShutdownHook = true;
		IGrpcServerStarter server = new GrpcServerStarter(port, withShutdownHook);
		
		// register greeter service
		GreeterServiceGrpcImpl greeterService = new GreeterServiceGrpcImpl();
		server.registerBeforeStart(greeterService);
		
		server.start();
		boolean blockInOtherThread = false;
		server.blockUntilShutdown(blockInOtherThread);
	}

}