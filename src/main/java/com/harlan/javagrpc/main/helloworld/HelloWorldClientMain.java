package com.harlan.javagrpc.main.helloworld;

import com.halran.javagrpc.grpc.artifact.GrpcConfiguration;
import com.halran.javagrpc.grpc.artifact.GrpcManagedChannel;
import com.halran.javagrpc.grpc.artifact.IGrpcManagedChannel;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldClientMain {

	private static final Logger log = LoggerFactory.getLogger(HelloWorldClientMain.class);
	
	public static void main(String[] args) throws InterruptedException {
		
		// create managed channel
		String host = "127.0.0.1";
		int port = 50051;
		IGrpcManagedChannel managedChannel = new GrpcManagedChannel(GrpcConfiguration.from(host, port));
		
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
		log.info("hello" + response.getHelloReply(0).getMessage());
		
//		HelloReply response2 = blockingStub.sayWorld(request);
//		log.info("world" + response2.getMessage());
	}
	
}