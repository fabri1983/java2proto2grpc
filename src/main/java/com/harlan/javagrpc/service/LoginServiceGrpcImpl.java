package com.harlan.javagrpc.service;

import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceImplBase;
import com.harlan.javagrpc.service.contract.protobuf.getResRequest;
import com.harlan.javagrpc.service.contract.protobuf.getResResponse;
import com.harlan.javagrpc.service.contract.protobuf.loginRequest;
import com.harlan.javagrpc.service.contract.protobuf.loginResponse;

import io.grpc.stub.StreamObserver;

import net.badata.protobuf.converter.Converter;

public class LoginServiceGrpcImpl extends LoginServiceImplBase {

	private LoginBusiness loginBusiness;
	
	public LoginServiceGrpcImpl(LoginBusiness loginBusiness) {
		super();
		this.loginBusiness = loginBusiness;
	}

	@Override
	public void login(loginRequest request, StreamObserver<loginResponse> responseObserver) {
		
		// convert protobuf type to domain model object
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = request.getRequest();
		com.halran.javagrpc.model.Request modelRequest = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request.class, requestProto);
		
		// Here you can use your domain model objects and call your business layer
		int loginId = loginBusiness.login(modelRequest);
		
		// there is no domain model to protobuf conversion because LoginService.login() returns just an int
		loginResponse response = loginResponse.newBuilder()
				.setInt(loginId)
				.build();
		
		// send it to the client
		responseObserver.onNext(response);
		// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
		responseObserver.onCompleted();
	}

	@Override
	public void getRes(getResRequest request, StreamObserver<getResResponse> responseObserver) {
		
		// convert protobuf type to domain model object
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = request.getRequest();
		com.halran.javagrpc.model.Request requestModel = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request.class, requestProto);
		
		// convert protobuf type to domain model object
		com.harlan.javagrpc.service.contract.protobuf.Request2 request2Proto = request.getRequest2();
		com.halran.javagrpc.model.Request2 request2Model = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request2.class, request2Proto);
		
		// Here you can use your domain model objects and call your business layer
		com.halran.javagrpc.model.Response responseModel = loginBusiness.getRes(requestModel, request2Model);
		
		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.Response responseProto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.Response.class, responseModel);
		
		// wrap the protobuf object
		getResResponse response = getResResponse.newBuilder()
				.setResponse(responseProto)
				.build();
		
		// send it to the client
		responseObserver.onNext(response);
		// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
		responseObserver.onCompleted();
	}

}
