package org.fabri1983.javagrpc.service.grpc.client;

import com.google.protobuf.Empty;

import org.fabri1983.javagrpc.grpc.artifact.client.GrpcClientStub;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import org.fabri1983.javagrpc.model.Request;
import org.fabri1983.javagrpc.model.Request2;
import org.fabri1983.javagrpc.model.Response;
import org.fabri1983.javagrpc.protobuf.converter.Converter;
import org.fabri1983.javagrpc.service.contract.LoginService;
import org.fabri1983.javagrpc.service.contract.protobuf.GetResProtoIn;
import org.fabri1983.javagrpc.service.contract.protobuf.GetResProtoOut;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginProtoIn;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginProtoOut;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceStub;
import org.fabri1983.javagrpc.service.contract.protobuf.Request2Proto;
import org.fabri1983.javagrpc.service.contract.protobuf.RequestProto;

public class LoginServiceGrpcClientStub 
		extends GrpcClientStub<LoginServiceBlockingStub, LoginServiceStub, LoginServiceFutureStub> 
		implements LoginService {

	LoginServiceGrpcClientStub(IGrpcManagedChannel managedChannel, 
			LoginServiceGrpcClientStubFactory clientStubFactory) {
		super(managedChannel, clientStubFactory);
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
