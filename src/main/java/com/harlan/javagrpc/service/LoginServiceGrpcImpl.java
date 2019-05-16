package com.harlan.javagrpc.service;

import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceImplBase;
import com.harlan.javagrpc.service.contract.protobuf.Response;
import com.harlan.javagrpc.service.contract.protobuf.getResRequest;
import com.harlan.javagrpc.service.contract.protobuf.getResResponse;
import com.harlan.javagrpc.service.contract.protobuf.loginRequest;
import com.harlan.javagrpc.service.contract.protobuf.loginResponse;

import io.grpc.stub.StreamObserver;

import net.badata.protobuf.converter.Converter;

public class LoginServiceGrpcImpl extends LoginServiceImplBase {

	// TODO define login business field and inject by constructor
	
	@Override
	public void login(loginRequest request, StreamObserver<loginResponse> responseObserver) {
		
		// convert protobuf types to domain model objects
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = request.getRequest();
		com.halran.javagrpc.model.Request modelRequest = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request.class, requestProto);
		
		// Here you can use your domain model objects and call your business layer
		int loginId = 1234567890;
		
		loginResponse response = loginResponse.newBuilder()
				.setInt(loginId)
				.build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void getRes(getResRequest request, StreamObserver<getResResponse> responseObserver) {
		
		// convert protobuf types to domain model objects
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = request.getRequest();
		com.halran.javagrpc.model.Request requestModel = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request.class, requestProto);
		
		com.harlan.javagrpc.service.contract.protobuf.Request2 request2Proto = request.getRequest2();
		com.halran.javagrpc.model.Request2 request2Model = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request2.class, request2Proto);
		
		// Here you can use your domain model objects and call your business layer
		com.halran.javagrpc.model.Response responseModel = com.halran.javagrpc.model.Response.from(1234567890, "Steeeeeve");
		
		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.Response responseProto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.Response.class, responseModel);
		
		getResResponse response = getResResponse.newBuilder()
				.setResponse(responseProto)
				.build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}
