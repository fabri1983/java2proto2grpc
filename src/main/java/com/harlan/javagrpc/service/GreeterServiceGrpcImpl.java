package com.harlan.javagrpc.service;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply.Builder;
import io.grpc.stub.StreamObserver;

// 实现 定义一个实现服务接口的类 
public class GreeterServiceGrpcImpl extends GreeterGrpc.GreeterImplBase {
	
	@Override
	public void sayHello(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
		System.out.println("sayHello service:" + request.getHelloRequest(0).getName());
		String message = request.getHelloRequest(0).getName();
		Builder replyProto = SearchResponse.HelloReply.newBuilder()
				.setMessage("hello" + message);
		SearchResponse reply = SearchResponse.newBuilder()
				.addHelloReply(replyProto)
				.build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

//	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
//		System.out.println("sayHello service:" + req.getName());
//		HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
//		responseObserver.onNext(reply);
//		responseObserver.onCompleted();
//	}
//
//	public void sayWorld(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
//		System.out.println("sayWorld service:" + request.getName());
//		HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + request.getName())).build();
//		responseObserver.onNext(reply);
//		responseObserver.onCompleted();
//	}
	
}

