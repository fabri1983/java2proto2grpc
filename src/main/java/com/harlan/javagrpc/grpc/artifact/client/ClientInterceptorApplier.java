package com.harlan.javagrpc.grpc.artifact.client;

import io.grpc.ClientInterceptor;

public interface ClientInterceptorApplier<T> {

	T apply(T stub, ClientInterceptor... interceptors);
	
}
