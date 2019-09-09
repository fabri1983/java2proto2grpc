package org.fabri1983.javagrpc.main.greeter;

import org.fabri1983.javagrpc.grpc.artifact.GrpcConfiguration;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.GrpcManagedChannel;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import org.fabri1983.javagrpc.service.contract.GreeterService;
import org.fabri1983.javagrpc.service.grpc.client.GreeterServiceGrpcClientStubFactory;
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
		GreeterService greeterService = GreeterServiceGrpcClientStubFactory.newFactory()
				.withManagedChannel(managedChannel)
				.build();
		
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