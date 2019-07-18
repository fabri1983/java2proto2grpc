package com.harlan.javagrpc.service.grpc.client;

import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStub;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import com.harlan.javagrpc.service.contract.GreeterService;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

public class GreeterServiceGrpcClientProxy 
		extends GrpcClientStub<GreeterBlockingStub, GreeterStub, GreeterFutureStub> 
		implements GreeterService {

	public GreeterServiceGrpcClientProxy(IGrpcManagedChannel managedChannel) {
		super(managedChannel, new GreeterGrpcClientStubFactory());
	}

	@Override
	public String sayHello(String message) {
		SearchResponse searchResponse = just( () -> {
			
			SearchRequest requestProto = SearchRequest.newBuilder()
					.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
					.setName(message))
					.build();
			
			SearchResponse responseProto = getBlockingStub().sayHello(requestProto);
			return responseProto;
		});
		
		return searchResponse.getHelloReply(0).getMessage();
	}

}
