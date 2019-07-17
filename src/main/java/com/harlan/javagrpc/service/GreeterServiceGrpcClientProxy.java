package com.harlan.javagrpc.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStub;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import com.harlan.javagrpc.service.contract.GreeterService;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

public class GreeterServiceGrpcClientProxy 
		extends GrpcClientStub<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> 
		implements GreeterService {

	public GreeterServiceGrpcClientProxy(IGrpcManagedChannel managedChannel) {
		super(managedChannel, GreeterGrpc.class);
	}

	@Override
	public String sayHello(String message) {
		SearchResponse searchResponse = withRateLimiter( () -> {
			
			SearchRequest requestProto = SearchRequest.newBuilder()
					.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
					.setName(message))
					.build();
			
			ListenableFuture<SearchResponse> responseProto = getFutureStub().sayHello(requestProto);
			return responseProto;
		});
		
		return searchResponse.getHelloReply(0).getMessage();
	}

}
