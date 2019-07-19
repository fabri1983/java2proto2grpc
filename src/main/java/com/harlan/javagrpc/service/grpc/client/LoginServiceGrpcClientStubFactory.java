package com.harlan.javagrpc.service.grpc.client;

import com.harlan.javagrpc.grpc.artifact.client.ClientInterceptorApplier;
import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStubFactory;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;

public class LoginServiceGrpcClientStubFactory 
		extends GrpcClientStubFactory<LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> {

	public LoginServiceGrpcClientStubFactory() {
		super((ClientInterceptor[])null);
	}
	
	public LoginServiceGrpcClientStubFactory(ClientInterceptor... interceptors) {
		super(interceptors);
	}

	@Override
	public LoginServiceBlockingStub newBlockingStub(ManagedChannel channel) {
		ClientInterceptorApplier<LoginServiceBlockingStub> applier = null;
		return applyInterceptors(LoginServiceGrpc.newBlockingStub(channel), applier);
	}

	@Override
	public LoginServiceStub newAsyncStub(ManagedChannel channel) {
		return LoginServiceGrpc.newStub(channel);
	}

	@Override
	public LoginServiceFutureStub newFutureStub(ManagedChannel channel) {
		return LoginServiceGrpc.newFutureStub(channel);
	}

	/**
	 * Client Intercepter Applier for LoginServiceBlockingStub.
	 */
	public class LoginServiceBlockingStubClientInterceptorApplier 
			implements ClientInterceptorApplier<LoginServiceBlockingStub> {

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
	public class LoginServiceStubClientInterceptorApplier 
			implements ClientInterceptorApplier<LoginServiceStub> {
		
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
	public class LoginServiceFutureStubClientInterceptorApplier 
			implements ClientInterceptorApplier<LoginServiceFutureStub> {
		
		@Override
		public LoginServiceFutureStub apply(LoginServiceFutureStub stub, ClientInterceptor... interceptors) {
			if (interceptors == null || interceptors.length == 0) {
				return stub;
			}
			return stub.withInterceptors(interceptors);
		}
	}
	
}
