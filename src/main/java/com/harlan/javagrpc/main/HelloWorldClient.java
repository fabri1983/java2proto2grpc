package com.harlan.javagrpc.main;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.SearchRequest;
import io.grpc.examples.helloworld.SearchResponse;

import java.util.concurrent.TimeUnit;

public class HelloWorldClient {

	private final ManagedChannel channel;
	private final GreeterGrpc.GreeterBlockingStub blockingStub;

	public HelloWorldClient(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

		blockingStub = GreeterGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public void greet(String name) {
		SearchRequest request = SearchRequest.newBuilder().addHelloRequest(SearchRequest.HelloRequest.newBuilder().setName(name)).build();
		SearchResponse response = blockingStub.sayHello(request);
		//HelloReply response2 = blockingStub.sayWorld(request);
		System.out.println("hello" + response.getHelloReply(0).getMessage());
//		System.out.println("world" + response2.getMessage());
	}

	public static void main(String[] args) throws InterruptedException {
		HelloWorldClient client = new HelloWorldClient("127.0.0.1", 50051);
		for (int i = 0; i < 5; i++) {
			client.greet("world:" + i);
		}
		client.shutdown();

	}
}