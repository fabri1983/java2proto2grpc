package org.fabri1983.javagrpc.grpc.artifact.client;

import io.grpc.ClientInterceptor;
import io.grpc.stub.AbstractStub;

public abstract class GrpcClientStubFactoryAbstract<B, A, F> implements IGrpcClientStubFactory<B, A, F> {

	private ClientInterceptor[] interceptors;
	
	public GrpcClientStubFactoryAbstract() {
	}
	
	public GrpcClientStubFactoryAbstract(ClientInterceptor... interceptors) {
		this.interceptors = interceptors;
	}
	
	protected <T extends AbstractStub<T>> T withInterceptors(T stub) {
		// if no interceptors then return the stub as it is
		if (interceptors == null || interceptors.length == 0) {
			return stub;
		}
		// otherwise add the interceptors
		return stub.withInterceptors(interceptors);
	}
	
}
