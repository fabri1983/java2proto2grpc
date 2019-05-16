package com.harlan.javagrpc.service;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;

public class LoginServiceRemoteProxy implements LoginService {

	// TODO add grpc client field here and inject by constructor
	
	public LoginServiceRemoteProxy() {
		super();
	}

	@Override
	public int login(Request req) {
		
		// TODO convert domain model objects to protobuf objects
		
		// TODO use the grpc client to call login()
		
		// TODO convert protobuf to domain model objects
		
		return 0;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {

		// TODO convert domain model objects to protobuf objects
		
		// TODO use the grpc client to call getRes()

		// TODO convert protobuf to domain model objects
		
		return null;
	}

}
