package com.harlan.javagrpc.service.contract;

import com.harlan.javagrpc.converter.annotation.GrpcEnabled;
import com.harlan.javagrpc.model.Request;
import com.harlan.javagrpc.model.Request2;
import com.harlan.javagrpc.model.Response;

@GrpcEnabled
public interface LoginService {
	
	public void loginVoid();
	
	public int login(Request req);
	
	public Response getRes(Request req, Request2 req2);

}
