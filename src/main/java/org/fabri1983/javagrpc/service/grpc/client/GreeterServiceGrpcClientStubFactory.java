package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;

import org.fabri1983.javagrpc.grpc.artifact.client.ClientInterceptorApplier;
import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStubFactoryAbstract;

public class GreeterServiceGrpcClientStubFactory
		extends GrpcClientStubFactoryAbstract<GreeterBlockingStub, GreeterStub, GreeterFutureStub> {

	public GreeterServiceGrpcClientStubFactory() {
		super();
	}
	
	public GreeterServiceGrpcClientStubFactory(ClientInterceptor... interceptors) {
		super(interceptors);
	}
	
	@Override
	public GreeterBlockingStub newBlockingStub(ManagedChannel channel) {
		ClientInterceptorApplier<GreeterBlockingStub> applier = 
				GreeterBlockingStubClientInterceptorApplier.newOne();
		return withInterceptors(GreeterGrpc.newBlockingStub(channel), applier);
	}

	@Override
	public GreeterStub newAsyncStub(ManagedChannel channel) {
		ClientInterceptorApplier<GreeterStub> applier = 
				GreeterStubClientInterceptorApplier.newOne();
		return withInterceptors(GreeterGrpc.newStub(channel), applier);
	}

	@Override
	public GreeterFutureStub newFutureStub(ManagedChannel channel) {
		ClientInterceptorApplier<GreeterFutureStub> applier = 
				GreeterFutureStubClientInterceptorApplier.newOne();
		return withInterceptors(GreeterGrpc.newFutureStub(channel), applier);
	}

	/**
	 * Client Intercepter Applier for GreeterBlockingStub.
	 */
	public static class GreeterBlockingStubClientInterceptorApplier 
			implements ClientInterceptorApplier<GreeterBlockingStub> {

		public static ClientInterceptorApplier<GreeterBlockingStub> newOne() {
			return new GreeterBlockingStubClientInterceptorApplier();
		}
		
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
	public static class GreeterStubClientInterceptorApplier 
			implements ClientInterceptorApplier<GreeterStub> {
		
		public static ClientInterceptorApplier<GreeterStub> newOne() {
			return new GreeterStubClientInterceptorApplier();
		}
		
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
	public static class GreeterFutureStubClientInterceptorApplier 
			implements ClientInterceptorApplier<GreeterFutureStub> {
		
		public static ClientInterceptorApplier<GreeterFutureStub> newOne() {
			return new GreeterFutureStubClientInterceptorApplier();
		}
		
		@Override
		public GreeterFutureStub apply(GreeterFutureStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
}
