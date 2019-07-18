package com.harlan.javagrpc.service.grpc.client;

import com.harlan.javagrpc.grpc.artifact.client.IGrpcClientStubFactory;

import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;

public class GreeterGrpcClientStubFactory
		implements IGrpcClientStubFactory<GreeterBlockingStub, GreeterStub, GreeterFutureStub> {

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

}
