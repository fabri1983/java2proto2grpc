package com.harlan.javagrpc.main.helloworld;

import com.halran.javagrpc.grpc.server.GrpcServerStarter;
import com.harlan.javagrpc.service.GreeterServiceGrpcImpl;

import java.io.IOException;

public class HelloWorldServerMain {

	public static void main(String[] args) throws IOException {
		int port = 50051;
		GrpcServerStarter server = new GrpcServerStarter(port);
		
		// register greeter service
		GreeterServiceGrpcImpl greeterService = new GreeterServiceGrpcImpl();
		server.register(greeterService);
		
		server.start();
		server.blockUntilShutdown(false);
	}

}