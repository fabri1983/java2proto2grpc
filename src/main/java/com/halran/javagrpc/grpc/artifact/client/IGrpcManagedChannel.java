package com.halran.javagrpc.grpc.artifact.client;

import io.grpc.ManagedChannel;

public interface IGrpcManagedChannel {

	public String getHost();

	public int getPort();
	
	String getTargetAddress();
	
	ManagedChannel getChannel();

	void shutdown();

}
