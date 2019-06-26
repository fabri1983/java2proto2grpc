package com.harlan.javagrpc.service;

import com.halran.javagrpc.grpc.artifact.GrpcServiceMarker;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply.Builder;
import io.grpc.stub.StreamObserver;

// 实现 定义一个实现服务接口的类 
public class GreeterServiceGrpcImpl extends GreeterGrpc.GreeterImplBase implements GrpcServiceMarker {
	
	public GreeterServiceGrpcImpl() {
		super();
	}

	@Override
	public void sayHello(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
		grcpTryCatch( responseObserver, () -> {
			
			String message = request.getHelloRequest(0).getName();
			Builder replyProto = SearchResponse.HelloReply.newBuilder()
					.setMessage(message);
			SearchResponse reply = SearchResponse.newBuilder()
					.addHelloReply(replyProto)
					.build();
			
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		});
	}

//	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
//		grcpTryCatch( responseObserver, () -> {
//
//			HelloReply reply = HelloReply.newBuilder().setMessage((req.getName())).build();
	
//			responseObserver.onNext(reply);
//			responseObserver.onCompleted();
//		});
//	}
//
//	public void sayWorld(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
//		grcpTryCatch( responseObserver, () -> {
//
//			HelloReply reply = HelloReply.newBuilder().setMessage((request.getName())).build();
	
//			responseObserver.onNext(reply);
//			responseObserver.onCompleted();
//		});
//	}
	
}

