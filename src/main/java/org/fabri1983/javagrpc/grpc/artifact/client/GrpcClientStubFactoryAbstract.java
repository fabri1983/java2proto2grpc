package org.fabri1983.javagrpc.grpc.artifact.client;

import io.grpc.ClientInterceptor;

public abstract class GrpcClientStubFactoryAbstract<B, A, F> implements IGrpcClientStubFactory<B, A, F> {

	private ClientInterceptor[] interceptors;
	
	public GrpcClientStubFactoryAbstract() {
	}
	
	public GrpcClientStubFactoryAbstract(ClientInterceptor... interceptors) {
		this.interceptors = interceptors;
	}
	
	protected <T> T withInterceptors(T stub, ClientInterceptorApplier<T> applier) {
		if (applier == null || interceptors == null || interceptors.length == 0) {
			return stub;
		}
		return applier.apply(stub, interceptors);
	}
	
}
