package com.halran.javagrpc.grpc.artifact;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class GrpcManagedChannel {

	private final ManagedChannel channel;

	public GrpcManagedChannel(String host, int port) {
		channel = createManagedChannel(host, port);
	}
	
	private ManagedChannel createManagedChannel(String host, int port) {
		return ManagedChannelBuilder
				.forAddress(host, port)
				// use plain text if your entire microservice ecosystem is inside a controlled network, 
				// otherwise setup your security artifacts such as key/trust stores
				.usePlaintext() 
				.build();
	}
	
	public ManagedChannel getChannel() {
		return channel;
	}

	public void shutdown() {
		try {
			System.out.println("*** Client shutdown.");
			channel.shutdown()
					.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			System.err.println();
		}
	}
	
}
