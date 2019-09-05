package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;

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
		return withInterceptors(LoginServiceGrpc.newBlockingStub(channel));
	}

	@Override
	public LoginServiceStub newAsyncStub(ManagedChannel channel) {
		return withInterceptors(LoginServiceGrpc.newStub(channel));
	}

	@Override
	public LoginServiceFutureStub newFutureStub(ManagedChannel channel) {
		return withInterceptors(LoginServiceGrpc.newFutureStub(channel));
	}
	
}
