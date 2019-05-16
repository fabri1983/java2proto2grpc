package com.harlan.javagrpc.service;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;

import net.badata.protobuf.converter.Converter;

public class LoginServiceRemoteProxy implements LoginService {

	private LoginServiceBlockingStub blockingStub;
	
	public LoginServiceRemoteProxy(LoginServiceBlockingStub blockingStub) {
		super();
		this.blockingStub = blockingStub;
	}

	@Override
	public int login(Request req) {
		
		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.loginRequest requestProto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.loginRequest.class, req);
		
		// use the grpc client to call login()
		com.harlan.javagrpc.service.contract.protobuf.loginResponse loginResponse = blockingStub.login(requestProto);
		
		// no protobuf to domain model conversion since we expect an int
		int login = loginResponse.getInt();
		
		return login;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {

		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.Request.class, req);
		
		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.Request2 request2Proto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.Request2.class, req2);
		
		com.harlan.javagrpc.service.contract.protobuf.getResRequest resRequest = 
				com.harlan.javagrpc.service.contract.protobuf.getResRequest.newBuilder()
					.setRequest(requestProto)
					.setRequest2(request2Proto)
					.build();
		
		// use the grpc client to call getRes()
		com.harlan.javagrpc.service.contract.protobuf.getResResponse resResponse = blockingStub.getRes(resRequest);
		
		// convert protobuf to domain model objects
		com.halran.javagrpc.model.Response modelResponse = Converter.create()
				.toDomain(com.halran.javagrpc.model.Response.class, resResponse);
		
		return modelResponse;
	}

}
