package org.fabri1983.javagrpc.service.grpc.server;

import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterImplBase;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply.Builder;
import io.grpc.stub.StreamObserver;

import org.fabri1983.javagrpc.business.contract.GreeterBusiness;
import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServiceMarker;
 
public class GreeterServiceGrpcServer extends GreeterImplBase implements GrpcServiceMarker {
	
	private GreeterBusiness greeterBusiness;
	
	public GreeterServiceGrpcServer(GreeterBusiness greeterBusiness) {
		super();
		this.greeterBusiness = greeterBusiness;
	}
	
	public GreeterServiceGrpcServer(GreeterBusiness greeterBusiness, ServerInterceptor... interceptors) {
		super();
		this.greeterBusiness = greeterBusiness;
		ServerInterceptors.intercept(this.bindService(), interceptors);
	}
	
	@Override
	public void sayHello(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
		grcpTryCatch( responseObserver, () -> {
			
			String message = request.getHelloRequest(0).getName();
			String messageReply = greeterBusiness.sayHello(message);
			
			Builder replyProto = SearchResponse.HelloReply.newBuilder()
					.setMessage(messageReply);
			SearchResponse reply = SearchResponse.newBuilder()
					.addHelloReply(replyProto)
					.build();
			
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		});
	}
	
}

