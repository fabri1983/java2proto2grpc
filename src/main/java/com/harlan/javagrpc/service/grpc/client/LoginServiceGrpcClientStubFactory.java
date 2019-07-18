package com.harlan.javagrpc.service.grpc.client;

import com.harlan.javagrpc.grpc.artifact.client.IGrpcClientStubFactory;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;

import io.grpc.ManagedChannel;

public class LoginServiceGrpcClientStubFactory 
		implements IGrpcClientStubFactory<LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> {

	@Override
	public LoginServiceBlockingStub newBlockingStub(ManagedChannel channel) {
		return LoginServiceGrpc.newBlockingStub(channel);
	}

	@Override
	public LoginServiceStub newAsyncStub(ManagedChannel channel) {
		return LoginServiceGrpc.newStub(channel);
	}

	@Override
	public LoginServiceFutureStub newFutureStub(ManagedChannel channel) {
		return LoginServiceGrpc.newFutureStub(channel);
	}

}
