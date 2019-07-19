package com.harlan.javagrpc.grpc.artifact.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;

public abstract class GrpcClientStubFactory<B, A, F> {

	private ClientInterceptor[] interceptors;
	
	public GrpcClientStubFactory(ClientInterceptor... interceptors) {
		this.interceptors = interceptors;
	}

	public abstract B newBlockingStub(ManagedChannel channel);

	public abstract A newAsyncStub(ManagedChannel channel);
	
	public abstract F newFutureStub(ManagedChannel channel);

	protected <T> T applyInterceptors(T stub, ClientInterceptorApplier<T> applier) {
		if (applier == null || interceptors == null || interceptors.length == 0) {
			return stub;
		}
		return applier.apply(stub, interceptors);
	}
	
}
