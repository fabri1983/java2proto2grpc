package com.harlan.javagrpc.service.grpc.client;

import com.google.protobuf.Empty;
import com.harlan.javagrpc.grpc.artifact.client.GrpcClientStub;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import com.harlan.javagrpc.model.Request;
import com.harlan.javagrpc.model.Request2;
import com.harlan.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.GetResProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoIn;
import com.harlan.javagrpc.service.contract.protobuf.LoginProtoOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;
import com.harlan.javagrpc.service.contract.protobuf.Request2Proto;
import com.harlan.javagrpc.service.contract.protobuf.RequestProto;

import net.badata.protobuf.converter.Converter;

public class LoginServiceGrpcClientProxy 
	extends GrpcClientStub<LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> 
	implements LoginService {

	public LoginServiceGrpcClientProxy(IGrpcManagedChannel managedChannel) {
		super(managedChannel, new LoginServiceGrpcClientStubFactory());
	}
	
	@Override
	public void loginVoid() {
		just( () -> {
			
			Empty requestProto = Empty.newBuilder().build();
			
			// use the grpc client to call loginVoid()
			Empty responseProto = getBlockingStub().loginVoid(requestProto);
			return responseProto;
		});
	}

	@Override
	public int login(Request req) {
		LoginProtoOut loginResponse = just( () -> {
			
			// convert domain model into protobuf object
			RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, req);
			
			// wrap the protobuf object
			LoginProtoIn loginRequestProto = LoginProtoIn.newBuilder()
					.setReq(requestProto)
					.build();
			
			// use the grpc client to call login()
			LoginProtoOut responseProto = getBlockingStub().login(loginRequestProto);
			return responseProto;
		});
		
		// no protobuf to domain model conversion since we expect an int
		int login = loginResponse.getInt();
		return login;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {
		GetResProtoOut resResponse = just( () -> {
			
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
			GetResProtoOut responseProto = getBlockingStub().getRes(resRequest);
			return responseProto;
		});
		
		// convert protobuf to domain model objects
		Response modelResponse = Converter.create()
				.toDomain(Response.class, resResponse.getResponse());
		return modelResponse;
	}

}
