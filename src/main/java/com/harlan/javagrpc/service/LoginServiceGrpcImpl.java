package com.harlan.javagrpc.service;

import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceImplBase;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.harlan.javagrpc.service.contract.protobuf.Response;
import com.harlan.javagrpc.service.contract.protobuf.getResRequest;
import com.harlan.javagrpc.service.contract.protobuf.getResResponse;
import com.harlan.javagrpc.service.contract.protobuf.loginRequest;
import com.harlan.javagrpc.service.contract.protobuf.loginResponse;

import io.grpc.stub.StreamObserver;

public class LoginServiceGrpcImpl extends LoginServiceImplBase {

	@Override
	public void login(loginRequest request, StreamObserver<loginResponse> responseObserver) {
		
		// TODO use protobuf-converter to transform loginRequest to Request
		
		// Here you can call your business layer
		int loginId = 1234567890;
		
		loginResponse response = loginResponse.newBuilder()
				.setInt(loginId)
				.build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void getRes(getResRequest request, StreamObserver<getResResponse> responseObserver) {
		
		// TODO use protobuf-converter to transform getResRequest to Request and Request2
		
		// Here you can call your business layer
		
		// TODO replace this with protobuf-converter
		Response responseProto = Response.newBuilder()
				.setId(1234567890)
				.setName("Steeeeeve")
				.build();
		
		getResResponse response = getResResponse.newBuilder()
				.setResponse(responseProto)
				.build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}
