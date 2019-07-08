package com.harlan.javagrpc.service;

import com.google.protobuf.Empty;
import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStubProxy;
import com.harlan.javagrpc.grpc.artifact.client.IGrpcManagedChannel;
import com.harlan.javagrpc.model.Request;
import com.harlan.javagrpc.model.Request2;
import com.harlan.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;
import com.harlan.javagrpc.service.contract.protobuf.Request2Proto;
import com.harlan.javagrpc.service.contract.protobuf.RequestProto;

import net.badata.protobuf.converter.Converter;

public class LoginServiceGrpcClientProxy 
	extends GrpcClientStubProxy<LoginServiceGrpc, LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> 
	implements LoginService {

	public LoginServiceGrpcClientProxy(IGrpcManagedChannel managedChannel) {
		super(managedChannel, LoginServiceGrpc.class);
	}
	
	@Override
	public void loginVoid() {
		withLimitUse( () -> {
			
			Empty request = Empty.newBuilder().build();
			// use the grpc client to call loginVoid()
			return getFutureStub().loginVoid(request);
		});
	}

	@Override
	public int login(Request req) {
		LoginProtoOut loginResponse = withLimitUse( () -> {
			
			// convert domain model into protobuf object
			RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, req);
			
			// wrap the protobuf object
			LoginProtoIn loginRequestProto = LoginProtoIn.newBuilder()
					.setReq(requestProto)
					.build();
			
			// use the grpc client to call login()
			return getFutureStub().login(loginRequestProto);
		});
		
		// no protobuf to domain model conversion since we expect an int
		int login = loginResponse.getInt();
		return login;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {
		GetResProtoOut resResponse = withLimitUse( () -> {
			
			// convert domain model into protobuf object
			RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, req);
			
			// convert domain model into protobuf object
			Request2Proto request2Proto = Converter.create().toProtobuf(Request2Proto.class, req2);
			
			// wrap the protobuf objects
			GetResProtoIn resRequest = GetResProtoIn.newBuilder()
					.setReq(requestProto)
					.setReq2(request2Proto)
					.build();
			
			// use the grpc client to call getRes()
			return getFutureStub().getRes(resRequest);
		});
		
		// convert protobuf to domain model objects
		Response modelResponse = Converter.create()
				.toDomain(Response.class, resResponse.getResponse());
		return modelResponse;
	}

}
