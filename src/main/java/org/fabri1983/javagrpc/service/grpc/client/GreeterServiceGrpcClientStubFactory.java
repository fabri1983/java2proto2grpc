package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;

import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStub;
import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStubFactoryAbstract;

public class GreeterServiceGrpcClientStubFactory
		extends GrpcClientStubFactoryAbstract<GreeterBlockingStub, GreeterStub, GreeterFutureStub> {

	public static GreeterServiceGrpcClientStubFactory newFactory() {
		return new GreeterServiceGrpcClientStubFactory();
	}
	
	@Override
	protected String getGrpcClientName() {
		return GreeterServiceGrpcClientStub.class.getSimpleName();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends GrpcClientStub<?,?,?>> T innerBuild() {
		return (T) new GreeterServiceGrpcClientStub(managedChannel, this);
	}
	
	@Override
	public GreeterBlockingStub newBlockingStub(ManagedChannel channel) {
		return ifInterceptors(GreeterGrpc.newBlockingStub(channel));
	}

	@Override
	public GreeterStub newAsyncStub(ManagedChannel channel) {
		return ifInterceptors(GreeterGrpc.newStub(channel));
	}

	@Override
	public GreeterFutureStub newFutureStub(ManagedChannel channel) {
		return ifInterceptors(GreeterGrpc.newFutureStub(channel));
	}
	
}
