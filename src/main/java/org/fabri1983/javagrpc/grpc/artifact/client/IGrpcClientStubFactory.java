package org.fabri1983.javagrpc.grpc.artifact.client;

import io.grpc.ManagedChannel;

public interface IGrpcClientStubFactory<B, A, F> {

	B newBlockingStub(ManagedChannel channel);

	A newAsyncStub(ManagedChannel channel);
	
	F newFutureStub(ManagedChannel channel);

}
