package com.halran.javagrpc.grpc.artifact;

import io.grpc.ManagedChannel;

public interface IGrpcManagedChannel {

	ManagedChannel getChannel();

	void shutdown();

}
