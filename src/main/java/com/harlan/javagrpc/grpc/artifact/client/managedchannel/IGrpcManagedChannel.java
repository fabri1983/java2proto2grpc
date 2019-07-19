package com.harlan.javagrpc.grpc.artifact.client.managedchannel;

import io.grpc.ManagedChannel;

public interface IGrpcManagedChannel {

	public String getHost();

	public int getPort();
	
	String getTargetAddress();
	
	ManagedChannel getChannel();

	void shutdown();

}