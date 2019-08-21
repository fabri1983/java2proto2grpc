package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;

import org.fabri1983.javagrpc.grpc.artifact.client.ClientInterceptorApplier;
import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStubFactoryAbstract;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;

public class LoginServiceGrpcClientStubFactory 
		extends GrpcClientStubFactoryAbstract<LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> {

	public LoginServiceGrpcClientStubFactory() {
		super();
	}
	
	public LoginServiceGrpcClientStubFactory(ClientInterceptor... interceptors) {
		super(interceptors);
	}

	@Override
	public LoginServiceBlockingStub newBlockingStub(ManagedChannel channel) {
		ClientInterceptorApplier<LoginServiceBlockingStub> applier = 
				LoginServiceBlockingStubClientInterceptorApplier.newOne();
		return withInterceptors(LoginServiceGrpc.newBlockingStub(channel), applier);
	}

	@Override
	public LoginServiceStub newAsyncStub(ManagedChannel channel) {
		ClientInterceptorApplier<LoginServiceStub> applier = 
				LoginServiceStubClientInterceptorApplier.newOne();
		return withInterceptors(LoginServiceGrpc.newStub(channel), applier);
	}

	@Override
	public LoginServiceFutureStub newFutureStub(ManagedChannel channel) {
		ClientInterceptorApplier<LoginServiceFutureStub> applier = 
				LoginServiceFutureStubClientInterceptorApplier.newOne();
		return withInterceptors(LoginServiceGrpc.newFutureStub(channel), applier);
	}

	/**
	 * Client Intercepter Applier for LoginServiceBlockingStub.
	 */
	public static class LoginServiceBlockingStubClientInterceptorApplier 
			implements ClientInterceptorApplier<LoginServiceBlockingStub> {
		
		public static ClientInterceptorApplier<LoginServiceBlockingStub> newOne() {
			return new LoginServiceBlockingStubClientInterceptorApplier();
		}

		@Override
		public LoginServiceBlockingStub apply(LoginServiceBlockingStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
	/**
	 * Client Intercepter Applier for LoginServiceStub.
	 */
	public static class LoginServiceStubClientInterceptorApplier 
			implements ClientInterceptorApplier<LoginServiceStub> {
		
		public static ClientInterceptorApplier<LoginServiceStub> newOne() {
			return new LoginServiceStubClientInterceptorApplier();
		}
		
		@Override
		public LoginServiceStub apply(LoginServiceStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
	/**
	 * Client Intercepter Applier for LoginServiceFutureStub.
	 */
	public static class LoginServiceFutureStubClientInterceptorApplier 
			implements ClientInterceptorApplier<LoginServiceFutureStub> {
		
		public static ClientInterceptorApplier<LoginServiceFutureStub> newOne() {
			return new LoginServiceFutureStubClientInterceptorApplier();
		}
		
		@Override
		public LoginServiceFutureStub apply(LoginServiceFutureStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
}
