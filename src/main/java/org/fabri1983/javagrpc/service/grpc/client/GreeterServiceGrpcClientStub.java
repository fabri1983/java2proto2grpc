package org.fabri1983.javagrpc.service.grpc.client;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.ClientInterceptor;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStub;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import org.fabri1983.javagrpc.service.contract.GreeterService;

public class GreeterServiceGrpcClientStub 
		extends GrpcClientStub<GreeterBlockingStub, GreeterStub, GreeterFutureStub> 
		implements GreeterService {

	public GreeterServiceGrpcClientStub(IGrpcManagedChannel managedChannel) {
		super(managedChannel, new GreeterServiceGrpcClientStubFactory());
	}

	public GreeterServiceGrpcClientStub(IGrpcManagedChannel managedChannel, ClientInterceptor... interceptors) {
		super(managedChannel, new GreeterServiceGrpcClientStubFactory(interceptors));
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
