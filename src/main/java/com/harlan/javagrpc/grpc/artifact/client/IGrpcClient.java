package com.harlan.javagrpc.grpc.artifact.client;

/**
 * B (blocking), A (async), F (future).
 *
 * @param <B>
 * @param <A>
 * @param <F>
 */
public interface IGrpcClient<B, A, F> {

	void shutdown();

	B getBlockingStub();

	A getAsyncStub();
	
	F getFutureStub();
	
}
