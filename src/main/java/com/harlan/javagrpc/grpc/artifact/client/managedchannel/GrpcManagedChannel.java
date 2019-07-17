package com.harlan.javagrpc.grpc.artifact.client.managedchannel;

import com.harlan.javagrpc.grpc.artifact.GrpcConfiguration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcManagedChannel implements IGrpcManagedChannel {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String host;
	private int port;
	private final ManagedChannel channel;

	public GrpcManagedChannel(GrpcConfiguration config) {
		// NOTE: host and port may be missing if using a Service Discovery solution
		this.host = config.getHost();
		this.port = config.getPort();
		
		ManagedChannelBuilder<?> channelBuilder = createManagedChannelBuilder(config);
		channel = channelBuilder
				// substantial performance improvements. However, it also requires the application to not block under any circumstances.
				.directExecutor()
				.build();
	}
	
	protected ManagedChannelBuilder<?> createManagedChannelBuilder(GrpcConfiguration config) {
		return ManagedChannelBuilder
				.forAddress(config.getHost(), config.getPort())
				// use plain text if your entire microservice ecosystem is inside a controlled network, 
				// otherwise setup your security artifacts such as key/trust stores
				.usePlaintext();
	}
	
	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public String getTargetAddress() {
		return host + ":" +  port;
	}

	@Override
	public ManagedChannel getChannel() {
		return channel;
	}

	@Override
	public void shutdown() {
		try {
			if (!channel.isShutdown()) {
				log.info("Shutting down channel for {}", getTargetAddress());
			}
			channel.shutdown()
					.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			log.error("shutdown(): {}. {}", ex.getClass().getSimpleName(), ex.getMessage());
		}
	}
	
}
