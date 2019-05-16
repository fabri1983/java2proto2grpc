package com.harlan.javagrpc.service;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.LoginServiceGrpcImpl;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceImplBase;

public class LoginServiceRemote implements LoginService {

	private LoginServiceImplBase loginServiceGrpc;
	
	public LoginServiceRemote() {
		super();
		loginServiceGrpc = new LoginServiceGrpcImpl();
	}

	@Override
	public int login() {
		// TODO call loginServiceGrpc.login()
		return 0;
	}

	@Override
	public Response getRes(Request req, Request2 req2) {
		// TODO call loginServiceGrpc.getRes()
		return null;
	}

}
