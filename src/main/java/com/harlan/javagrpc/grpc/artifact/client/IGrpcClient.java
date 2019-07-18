package com.harlan.javagrpc.grpc.artifact.client;

public interface IGrpcClient<B, A, F> {

	void shutdown();

	B getBlockingStub();

	A getAsyncStub();
	
	F getFutureStub();
	
}
