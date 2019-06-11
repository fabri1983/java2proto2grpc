package com.harlan.javagrpc.main.helloworld;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

import java.util.concurrent.TimeUnit;

public class HelloWorldClientMain {

	private final ManagedChannel channel;
	private final GreeterGrpc.GreeterBlockingStub blockingGreeterStub;

	public HelloWorldClientMain(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		blockingGreeterStub = GreeterGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
	}

	public void greet(String name) {
		SearchRequest request = SearchRequest.newBuilder()
				.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
						.setName(name))
				.build();
		SearchResponse response = blockingGreeterStub.sayHello(request);
//		HelloReply response2 = blockingStub.sayWorld(request);
		System.out.println("hello" + response.getHelloReply(0).getMessage());
//		System.out.println("world" + response2.getMessage());
	}

	public static void main(String[] args) throws InterruptedException {
		HelloWorldClientMain client = new HelloWorldClientMain("127.0.0.1", 50051);
		for (int i = 0; i < 5; i++) {
			client.greet("world:" + i);
		}
		client.shutdown();
	}
	
}