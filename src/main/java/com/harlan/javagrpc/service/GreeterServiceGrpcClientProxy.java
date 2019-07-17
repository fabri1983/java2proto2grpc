package com.harlan.javagrpc.service;

import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStubProxy;
import com.harlan.javagrpc.grpc.artifact.client.IGrpcManagedChannel;
import com.harlan.javagrpc.service.contract.GreeterService;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

public class GreeterServiceGrpcClientProxy 
		extends GrpcClientStubProxy<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> 
		implements GreeterService {

	public GreeterServiceGrpcClientProxy(IGrpcManagedChannel managedChannel) {
		super(managedChannel, GreeterGrpc.class);
	}

	@Override
	public String sayHello(String message) {
		SearchResponse searchResponse = withRateLimiter( () -> {
			SearchRequest request = SearchRequest.newBuilder()
					.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
					.setName(message))
					.build();
			
			return getFutureStub().sayHello(request);
		});
		
		return searchResponse.getHelloReply(0).getMessage();
	}

}
