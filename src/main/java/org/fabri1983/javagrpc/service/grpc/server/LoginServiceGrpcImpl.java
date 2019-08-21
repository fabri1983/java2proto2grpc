package org.fabri1983.javagrpc.service.grpc.server;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;

import org.fabri1983.javagrpc.business.contract.LoginBusiness;
import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServiceMarker;
import org.fabri1983.javagrpc.model.Request;
import org.fabri1983.javagrpc.model.Request2;
import org.fabri1983.javagrpc.protobuf.converter.Converter;
import org.fabri1983.javagrpc.service.contract.protobuf.GetResProtoIn;
import org.fabri1983.javagrpc.service.contract.protobuf.GetResProtoOut;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginProtoIn;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginProtoOut;
import org.fabri1983.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceImplBase;
import org.fabri1983.javagrpc.service.contract.protobuf.Request2Proto;
import org.fabri1983.javagrpc.service.contract.protobuf.RequestProto;
import org.fabri1983.javagrpc.service.contract.protobuf.ResponseProto;

public class LoginServiceGrpcImpl extends LoginServiceImplBase implements GrpcServiceMarker {

	private LoginBusiness loginBusiness;
	
	public LoginServiceGrpcImpl(LoginBusiness loginBusiness) {
		super();
		this.loginBusiness = loginBusiness;
	}

	@Override
	public void loginVoid(Empty request, StreamObserver<Empty> responseObserver) {
		grcpTryCatch( responseObserver, () -> {
			
			loginBusiness.loginVoid();
			
			Empty response = Empty.newBuilder().build();
			
			// send it to the client
			responseObserver.onNext(response);
			// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
			responseObserver.onCompleted();
		});
		
	}
	
	@Override
	public void login(LoginProtoIn request, StreamObserver<LoginProtoOut> responseObserver) {
		grcpTryCatch( responseObserver, () -> {
			
			// convert protobuf type to domain model object
			RequestProto requestProto = request.getReq();
			Request modelRequest = Converter.create().toDomain(Request.class, requestProto);
			
			// Here you can use your domain model objects and call your business layer
			int loginId = loginBusiness.login(modelRequest);
			
			// there is no domain model to protobuf conversion because LoginService.login() returns just an int
			LoginProtoOut response = LoginProtoOut.newBuilder()
					.setInt(loginId)
					.build();
			
			// send it to the client
			responseObserver.onNext(response);
			// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
			responseObserver.onCompleted();
		});
	}

	@Override
	public void getRes(GetResProtoIn request, StreamObserver<GetResProtoOut> responseObserver) {
		grcpTryCatch( responseObserver, () -> {
			
			// convert protobuf type to domain model object
			RequestProto requestProto = request.getReq();
			Request requestModel = Converter.create().toDomain(Request.class, requestProto);
			
			// convert protobuf type to domain model object
			Request2Proto request2Proto = request.getReq2();
			Request2 request2Model = Converter.create().toDomain(Request2.class, request2Proto);
			
			// Here you can use your domain model objects and call your business layer
			org.fabri1983.javagrpc.model.Response responseModel = loginBusiness.getRes(requestModel, request2Model);
			
			// convert domain model into protobuf object
			ResponseProto responseProto = Converter.create().toProtobuf(ResponseProto.class, responseModel);
			
			// wrap the protobuf object
			GetResProtoOut response = GetResProtoOut.newBuilder()
					.setResponse(responseProto)
					.build();
			
			// send it to the client
			responseObserver.onNext(response);
			// finish dealing with RPC, else the connection will be hung, and client will wait for more information to come in
			responseObserver.onCompleted();
		});
	}

}
