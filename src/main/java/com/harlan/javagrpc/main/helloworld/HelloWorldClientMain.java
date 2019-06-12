package com.harlan.javagrpc.main.helloworld;

import com.halran.javagrpc.grpc.artifact.GrpcManagedChannel;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

public class HelloWorldClientMain {

	private static void greet(GreeterGrpc.GreeterBlockingStub blockingGreeterStub, String name) {
		SearchRequest request = SearchRequest.newBuilder()
				.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
				.setName(name))
				.build();
		
		SearchResponse response = blockingGreeterStub.sayHello(request);
		System.out.println("hello" + response.getHelloReply(0).getMessage());
		
//		HelloReply response2 = blockingStub.sayWorld(request);
//		System.out.println("world" + response2.getMessage());
	}

	public static void main(String[] args) throws InterruptedException {
		
		// create managed channel
		GrpcManagedChannel managedChannel = new GrpcManagedChannel("127.0.0.1", 50051);
		
		// create greeter proxy (stub)
		GreeterGrpc.GreeterBlockingStub blockingGreeterStub = 
				GreeterGrpc.newBlockingStub(managedChannel.getChannel());
		
		// call grpc stub
		for (int i = 0; i < 5; i++) {
			greet(blockingGreeterStub, "world:" + i);
		}
		
		managedChannel.shutdown();
	}
	
}