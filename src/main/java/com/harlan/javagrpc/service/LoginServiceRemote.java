package com.harlan.javagrpc.service;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.contract.LoginService;

public class LoginServiceRemote implements LoginService {

	// TODO add grpc client field here and inject by constructor
	
	public LoginServiceRemote() {
		super();
	}

	@Override
	public int login(Request req) {
		
		// TODO use protobuf-converter in order to transform model objects to protobuf messages and vice versa
		
		// TODO use the grpc client to call login
		
		return 0;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {

		// TODO use protobuf-converter in order to transform model objects to protobuf messages and vice versa
		
		// TODO use the grpc client to call getRes
		
		return null;
	}

}
