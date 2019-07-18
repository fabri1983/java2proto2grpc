package com.harlan.javagrpc.main.greeter;

import com.harlan.javagrpc.grpc.artifact.GrpcConfiguration;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.GrpcManagedChannel;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import com.harlan.javagrpc.service.contract.GreeterService;
import com.harlan.javagrpc.service.grpc.client.GreeterServiceGrpcClientStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterClientMain {

	private static final Logger log = LoggerFactory.getLogger(GreeterClientMain.class);
	
	public static void main(String[] args) throws InterruptedException {
		
		// create managed channel
		String host = "127.0.0.1";
		int port = 50051;
		IGrpcManagedChannel managedChannel = new GrpcManagedChannel(GrpcConfiguration.from(host, port));
		
		// create greeter service client stub
		GreeterService greeterService = new GreeterServiceGrpcClientStub(managedChannel);
		
		// call grpc stub
		for (int i = 0; i < 5; i++) {
			greet(greeterService, "world:" + i);
		}
		
		managedChannel.shutdown();
	}

	private static void greet(GreeterService greeterService, String message) {
		String messageResponse = greeterService.sayHello(message);
		log.info("Message: " + messageResponse);
	}
	
}