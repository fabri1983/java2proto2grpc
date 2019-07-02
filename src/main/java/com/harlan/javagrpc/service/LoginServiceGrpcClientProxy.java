package com.harlan.javagrpc.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.halran.javagrpc.grpc.artifact.GrpcClientProxyLimited;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.service.contract.protobuf.Request2Proto;
import com.harlan.javagrpc.service.contract.protobuf.RequestProto;

import java.util.function.Supplier;

import net.badata.protobuf.converter.Converter;

public class LoginServiceGrpcClientProxy extends GrpcClientProxyLimited implements LoginService {

	private LoginServiceFutureStub futureStub;
	private LoginServiceBlockingStub blockingStub;
	
	public LoginServiceGrpcClientProxy(LoginServiceFutureStub futureStub) {
		super();
		this.futureStub = futureStub;
	}

	public LoginServiceGrpcClientProxy(LoginServiceBlockingStub blockingStub) {
		super();
		this.blockingStub = blockingStub;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T callGrpc(Supplier<?> s) {
		if (futureStub != null) {
			return withLimitUse( (Supplier<ListenableFuture<T>>) s);
		}
		else if (blockingStub != null) {
			return (T) just(s);
		}
		return null;
	}
	
	@Override
	public void loginVoid() {
		callGrpc( () -> {
			
			Empty request = Empty.newBuilder().build();
			// use the grpc client to call loginVoid()
			return futureStub.loginVoid(request);
		});
	}

	@Override
	public int login(Request req) {
		LoginProtoOut loginResponse = callGrpc( () -> {
			
			// convert domain model into protobuf object
			RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, req);
			
			// wrap the protobuf object
			LoginProtoIn loginRequestProto = LoginProtoIn.newBuilder()
					.setReq(requestProto)
					.build();
			
			// use the grpc client to call login()
			return futureStub.login(loginRequestProto);
		});
		
		// no protobuf to domain model conversion since we expect an int
		int login = loginResponse.getInt();
		return login;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {
		GetResProtoOut resResponse = callGrpc( () -> {
			
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
			return futureStub.getRes(resRequest);
		});
		
		// convert protobuf to domain model objects
		Response modelResponse = Converter.create()
				.toDomain(Response.class, resResponse.getResponse());
		return modelResponse;
	}

}
