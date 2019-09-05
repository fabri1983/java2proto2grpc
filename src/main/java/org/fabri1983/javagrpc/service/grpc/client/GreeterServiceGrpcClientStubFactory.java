package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;

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
		return withInterceptors(GreeterGrpc.newBlockingStub(channel));
	}

	@Override
	public GreeterStub newAsyncStub(ManagedChannel channel) {
		return withInterceptors(GreeterGrpc.newStub(channel));
	}

	@Override
	public GreeterFutureStub newFutureStub(ManagedChannel channel) {
		return withInterceptors(GreeterGrpc.newFutureStub(channel));
	}
	
}
