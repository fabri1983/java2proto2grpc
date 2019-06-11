package com.harlan.javagrpc.main.helloworld;

import com.halran.javagrpc.server.GrpcServer;
import com.harlan.javagrpc.service.GreeterServiceGrpcImpl;

import java.io.IOException;

public class HelloWorldServerMain {

	public static void main(String[] args) throws IOException {
		int port = 50051;
		GrpcServer server = new GrpcServer(port);
		
		// register greeter service
		GreeterServiceGrpcImpl greeterService = new GreeterServiceGrpcImpl();
		server.register(greeterService);
		
		server.start();
		server.blockUntilShutdown(false);
	}

}