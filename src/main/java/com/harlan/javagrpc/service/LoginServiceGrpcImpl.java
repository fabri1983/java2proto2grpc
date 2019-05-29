package com.harlan.javagrpc.service;

import com.google.protobuf.Empty;
import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.service.contract.protobuf.GetResMessageIn;
import com.harlan.javagrpc.service.contract.protobuf.GetResMessageOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginMessageIn;
import com.harlan.javagrpc.service.contract.protobuf.LoginMessageOut;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceImplBase;

import io.grpc.stub.StreamObserver;

import net.badata.protobuf.converter.Converter;

public class LoginServiceGrpcImpl extends LoginServiceImplBase {

	private LoginBusiness loginBusiness;
	
	public LoginServiceGrpcImpl(LoginBusiness loginBusiness) {
		super();
		this.loginBusiness = loginBusiness;
	}

	@Override
	public void loginVoid(Empty request, StreamObserver<Empty> responseObserver) {
		
		loginBusiness.loginVoid();
		
		Empty response = Empty.newBuilder()
				.build();
		
		// send it to the client
		responseObserver.onNext(response);
		// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
		responseObserver.onCompleted();
	}
	
	@Override
	public void login(LoginMessageIn request, StreamObserver<LoginMessageOut> responseObserver) {
		
		// convert protobuf type to domain model object
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = request.getArg0();
		com.halran.javagrpc.model.Request modelRequest = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request.class, requestProto);
		
		// Here you can use your domain model objects and call your business layer
		int loginId = loginBusiness.login(modelRequest);
		
		// there is no domain model to protobuf conversion because LoginService.login() returns just an int
		LoginMessageOut response = LoginMessageOut.newBuilder()
				.setInt(loginId)
				.build();
		
		// send it to the client
		responseObserver.onNext(response);
		// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
		responseObserver.onCompleted();
	}

	@Override
	public void getRes(GetResMessageIn request, StreamObserver<GetResMessageOut> responseObserver) {
		
		// convert protobuf type to domain model object
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = request.getArg0();
		com.halran.javagrpc.model.Request requestModel = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request.class, requestProto);
		
		// convert protobuf type to domain model object
		com.harlan.javagrpc.service.contract.protobuf.Request2 request2Proto = request.getArg1();
		com.halran.javagrpc.model.Request2 request2Model = Converter.create()
				.toDomain(com.halran.javagrpc.model.Request2.class, request2Proto);
		
		// Here you can use your domain model objects and call your business layer
		com.halran.javagrpc.model.Response responseModel = loginBusiness.getRes(requestModel, request2Model);
		
		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.Response responseProto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.Response.class, responseModel);
		
		// wrap the protobuf object
		GetResMessageOut response = GetResMessageOut.newBuilder()
				.setResponse(responseProto)
				.build();
		
		// send it to the client
		responseObserver.onNext(response);
		// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
		responseObserver.onCompleted();
	}

}
