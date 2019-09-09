package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.ManagedChannel;

import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStub;
import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStubFactoryAbstract;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;

public class LoginServiceGrpcClientStubFactory 
		extends GrpcClientStubFactoryAbstract<LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> {

	public static LoginServiceGrpcClientStubFactory newFactory() {
		return new LoginServiceGrpcClientStubFactory();
	}
	
	@Override
	protected String getGrpcClientName() {
		return LoginServiceGrpcClientStub.class.getSimpleName();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends GrpcClientStub<?,?,?>> T innerBuild() {
		return (T) new LoginServiceGrpcClientStub(managedChannel, this);
	}

	@Override
	public LoginServiceBlockingStub newBlockingStub(ManagedChannel channel) {
		return ifInterceptors(LoginServiceGrpc.newBlockingStub(channel));
	}

	@Override
	public LoginServiceStub newAsyncStub(ManagedChannel channel) {
		return ifInterceptors(LoginServiceGrpc.newStub(channel));
	}

	@Override
	public LoginServiceFutureStub newFutureStub(ManagedChannel channel) {
		return ifInterceptors(LoginServiceGrpc.newFutureStub(channel));
	}
	
}
