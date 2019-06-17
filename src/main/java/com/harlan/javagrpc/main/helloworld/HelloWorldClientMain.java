package com.harlan.javagrpc.main.helloworld;

import com.halran.javagrpc.grpc.artifact.GrpcManagedChannel;
import com.halran.javagrpc.grpc.artifact.IGrpcManagedChannel;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

public class HelloWorldClientMain {

	public static void main(String[] args) throws InterruptedException {
		
		// create managed channel
		String host = "127.0.0.1";
		int port = 50051;
		IGrpcManagedChannel managedChannel = new GrpcManagedChannel(host, port);
		
		// create greeter proxy (stub)
		GreeterGrpc.GreeterBlockingStub blockingGreeterStub = 
				GreeterGrpc.newBlockingStub(managedChannel.getChannel());
		
		// call grpc stub
		for (int i = 0; i < 5; i++) {
			greet(blockingGreeterStub, "world:" + i);
		}
		
		managedChannel.shutdown();
	}

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
	
}