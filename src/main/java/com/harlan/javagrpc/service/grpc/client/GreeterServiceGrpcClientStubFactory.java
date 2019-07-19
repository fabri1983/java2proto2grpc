package com.harlan.javagrpc.service.grpc.client;

import com.harlan.javagrpc.grpc.artifact.client.ClientInterceptorApplier;
import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStubFactory;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;

public class GreeterServiceGrpcClientStubFactory
		extends GrpcClientStubFactory<GreeterBlockingStub, GreeterStub, GreeterFutureStub> {

	public GreeterServiceGrpcClientStubFactory() {
		super((ClientInterceptor[])null);
	}
	
	public GreeterServiceGrpcClientStubFactory(ClientInterceptor... interceptors) {
		super(interceptors);
	}
	
	@Override
	public GreeterBlockingStub newBlockingStub(ManagedChannel channel) {
		return GreeterGrpc.newBlockingStub(channel);
	}

	@Override
	public GreeterStub newAsyncStub(ManagedChannel channel) {
		return GreeterGrpc.newStub(channel);
	}

	@Override
	public GreeterFutureStub newFutureStub(ManagedChannel channel) {
		return GreeterGrpc.newFutureStub(channel);
	}

	/**
	 * Client Intercepter Applier for GreeterBlockingStub.
	 */
	public class GreeterBlockingStubClientInterceptorApplier 
			implements ClientInterceptorApplier<GreeterBlockingStub> {

		@Override
		public GreeterBlockingStub apply(GreeterBlockingStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
	/**
	 * Client Intercepter Applier for GreeterStub.
	 */
	public class GreeterStubClientInterceptorApplier 
			implements ClientInterceptorApplier<GreeterStub> {
		
		@Override
		public GreeterStub apply(GreeterStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
	/**
	 * Client Intercepter Applier for GreeterFutureStub.
	 */
	public class GreeterFutureStubClientInterceptorApplier 
			implements ClientInterceptorApplier<GreeterFutureStub> {
		
		@Override
		public GreeterFutureStub apply(GreeterFutureStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
}
