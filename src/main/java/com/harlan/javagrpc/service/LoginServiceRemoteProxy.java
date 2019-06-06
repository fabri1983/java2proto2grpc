package com.harlan.javagrpc.service;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import com.harlan.javagrpc.service.contract.protobuf.Request2Proto;
import com.harlan.javagrpc.service.contract.protobuf.RequestProto;

import net.badata.protobuf.converter.Converter;

public class LoginServiceRemoteProxy implements LoginService {

	private LoginServiceBlockingStub blockingStub;
	
	public LoginServiceRemoteProxy(LoginServiceBlockingStub blockingStub) {
		super();
		this.blockingStub = blockingStub;
	}

	@Override
	public void loginVoid() {
		
	}

	@Override
	public int login(Request req) {
		
		// convert domain model into protobuf object
		RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, req);
		
		// wrap the protobuf object
		LoginProtoIn loginRequestProto = LoginProtoIn.newBuilder()
				.setRequestArg0(requestProto)
				.build();
		
		// use the grpc client to call login()
		LoginProtoOut loginResponse = blockingStub.login(loginRequestProto);
		
		// no protobuf to domain model conversion since we expect an int
		int login = loginResponse.getInt();
		
		return login;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {

		// convert domain model into protobuf object
		RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, req);
		
		// convert domain model into protobuf object
		Request2Proto request2Proto = Converter.create().toProtobuf(Request2Proto.class, req2);
		
		// wrap the protobuf objects
		GetResProtoIn resRequest = GetResProtoIn.newBuilder()
				.setRequestArg0(requestProto)
				.setRequest2Arg1(request2Proto)
				.build();
		
		// use the grpc client to call getRes()
		GetResProtoOut resResponse = blockingStub.getRes(resRequest);
		
		// convert protobuf to domain model objects
		Response modelResponse = Converter.create()
				.toDomain(Response.class, resResponse.getResponse());
		
		return modelResponse;
	}

}
