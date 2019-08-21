package org.fabri1983.javagrpc.grpc.artifact.client;

import io.grpc.ManagedChannel;

public interface IGrpcClientStubFactory<B, A, F> {

	public abstract B newBlockingStub(ManagedChannel channel);

	public abstract A newAsyncStub(ManagedChannel channel);
	
	public abstract F newFutureStub(ManagedChannel channel);

}
