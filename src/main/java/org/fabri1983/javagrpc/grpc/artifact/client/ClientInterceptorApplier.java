package org.fabri1983.javagrpc.grpc.artifact.client;

import io.grpc.ClientInterceptor;

public interface ClientInterceptorApplier<T> {

	T apply(T stub, ClientInterceptor... interceptors);
	
}
